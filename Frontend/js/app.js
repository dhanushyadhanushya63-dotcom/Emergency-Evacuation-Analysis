/**
 * Main Application Controller & View Manager
 * Emergency Evacuation Route Simulation and Risk Analysis
 */

const App = {
    currentView: 'home',
    currentUser: {
        name: 'Chief Safety Commander',
        role: 'COMMANDER',
        avatar: '👨‍✈️'
    },
    building: null,
    scenarios: [],
    currentScenario: null,
    latestSimulation: null,
    floorPlanViewer: null,
    simulationEngine: null,

    async init() {
        console.log('Initializing Emergency Evacuation Application...');
        this.initEventListeners();

        try {
            ApiService.setLoading(true, 'Loading facility layout and emergency scenarios...');

            // Load initial building & scenarios
            this.building = await ApiService.getSampleBuilding();
            this.scenarios = await ApiService.getAllScenarios();

            // Set default scenario
            this.currentScenario = this.scenarios.find(s => s.isDefault) || this.scenarios[0];

            this.populateScenarioDropdowns();
            this.updateHeaderStats();

            // Initialize floor plan canvas viewer
            this.initCanvasViewer();

            // Switch to initial view based on URL hash or default to 'home'
            const initialRoute = window.location.hash ? window.location.hash.substring(1) : 'home';
            this.navigateTo(initialRoute);

        } catch (error) {
            console.error('Initialization error:', error);
            this.showToast('Initialization Error: ' + error.message, 'danger');
        } finally {
            ApiService.setLoading(false);
        }
    },

    initEventListeners() {
        // Sidebar item clicks
        document.querySelectorAll('.sidebar-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const view = item.getAttribute('data-view');
                if (view) {
                    this.navigateTo(view);
                }
            });
        });

        // Topbar scenario selector change
        const scenarioSelect = document.getElementById('top-scenario-select');
        if (scenarioSelect) {
            scenarioSelect.addEventListener('change', async (e) => {
                const scenarioId = parseInt(e.target.value);
                await this.selectScenario(scenarioId);
            });
        }

        // Custom Scenario creation form submit
        const createScenarioForm = document.getElementById('createScenarioForm');
        if (createScenarioForm) {
            createScenarioForm.addEventListener('submit', (e) => this.handleCreateScenario(e));
        }

        // Mobile menu toggle
        const toggleBtn = document.getElementById('sidebar-toggle-btn');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                const sidebar = document.getElementById('sidebar');
                if (sidebar) sidebar.classList.toggle('show');
            });
        }
    },

    initCanvasViewer() {
        this.floorPlanViewer = new FloorPlanViewer('floorplanCanvas');
        this.simViewer = new FloorPlanViewer('simCanvasHolder');
        this.dashboardViewer = new FloorPlanViewer('dashboardPreviewCanvas');

        // Attach simulation engine to the simulation tab canvas viewer
        this.simulationEngine = new SimulationEngine(this.simViewer);

        // Simulation engine callbacks
        this.simulationEngine.onProgress = (elapsed, evacuated, total) => {
            const timeElem = document.getElementById('sim-timer-display');
            if (timeElem) timeElem.innerText = `${elapsed.toFixed(1)}s`;

            const evacElem = document.getElementById('sim-evacuated-display');
            if (evacElem) evacElem.innerText = `${evacuated} / ${total}`;

            const bar = document.getElementById('sim-progress-bar');
            if (bar && total > 0) {
                const pct = Math.min(100, Math.round((evacuated / total) * 100));
                bar.style.width = `${pct}%`;
            }
        };

        this.simulationEngine.onComplete = (totalTime, evacuated) => {
            this.showToast(`Simulation completed! ${evacuated} occupants reached safe exits in ${totalTime.toFixed(1)}s.`, 'success');
        };

        // Floor plan interactive click callbacks
        const setupCallbacks = (viewer) => {
            if (!viewer) return;
            viewer.onRoomClick = (room) => {
                this.displayRoomDetails(room);
            };

            viewer.onCorridorClick = async (corridor) => {
                if (this.currentView === 'scenarios') {
                    await this.toggleCorridorBlock(corridor.id);
                } else {
                    this.showToast(`Corridor ${corridor.corridorCode}: ${corridor.name} (Width: ${corridor.widthMeters}m)`, 'info');
                }
            };

            viewer.onExitClick = async (exit) => {
                if (this.currentView === 'scenarios') {
                    await this.toggleExitAvailability(exit.id);
                } else {
                    this.showToast(`Exit ${exit.exitCode}: ${exit.name} (Capacity: ${exit.flowRateCapacity} pers/min)`, 'info');
                }
            };
        };

        setupCallbacks(this.floorPlanViewer);
        setupCallbacks(this.simViewer);
        setupCallbacks(this.dashboardViewer);

        this.updateViewerData();
    },

    updateViewerData() {
        if (!this.building) return;

        const floor = this.building.floors && this.building.floors[0] ? this.building.floors[0] : null;
        if (!floor) return;

        const blockedCorridors = [];
        const unavailableExits = [];

        if (this.currentScenario && this.currentScenario.blockedPassages) {
            this.currentScenario.blockedPassages.forEach(bp => {
                if (bp.corridorId) blockedCorridors.push(bp.corridorId);
                if (bp.exitId) unavailableExits.push(bp.exitId);
            });
        }

        const routes = (this.latestSimulation && this.latestSimulation.routes) ? this.latestSimulation.routes : [];

        [this.floorPlanViewer, this.simViewer, this.dashboardViewer].forEach(v => {
            if (v) {
                v.setData(
                    this.building,
                    floor.rooms,
                    floor.corridors,
                    floor.exits,
                    blockedCorridors,
                    unavailableExits,
                    routes
                );
            }
        });
    },

    populateScenarioDropdowns() {
        const topSelect = document.getElementById('top-scenario-select');
        const viewSelect = document.getElementById('config-scenario-select');

        [topSelect, viewSelect].forEach(select => {
            if (!select) return;
            select.innerHTML = '';
            this.scenarios.forEach(sc => {
                const opt = document.createElement('option');
                opt.value = sc.id;
                opt.innerText = sc.name;
                if (this.currentScenario && this.currentScenario.id === sc.id) {
                    opt.selected = true;
                }
                select.appendChild(opt);
            });
        });
    },

    updateHeaderStats() {
        const roomsCount = this.building && this.building.floors && this.building.floors[0] ? this.building.floors[0].rooms.length : 10;
        const corridorsCount = this.building && this.building.floors && this.building.floors[0] ? this.building.floors[0].corridors.length : 8;
        const exitsCount = this.building && this.building.floors && this.building.floors[0] ? this.building.floors[0].exits.length : 4;

        // Count total occupants
        let occupantsCount = 0;
        if (this.building && this.building.floors && this.building.floors[0]) {
            this.building.floors[0].rooms.forEach(r => occupantsCount += (r.defaultOccupants || 0));
        }

        // Apply crowd density multiplier if applicable
        if (this.currentScenario && this.currentScenario.crowdDensityMultiplier) {
            occupantsCount = Math.round(occupantsCount * this.currentScenario.crowdDensityMultiplier);
        }

        // Update DOM stats elements
        const ids = {
            'dash-stat-rooms': roomsCount,
            'dash-stat-corridors': corridorsCount,
            'dash-stat-exits': exitsCount,
            'dash-stat-occupants': occupantsCount
        };

        for (const [id, val] of Object.entries(ids)) {
            const el = document.getElementById(id);
            if (el) el.innerText = val;
        }
    },

    async selectScenario(scenarioId) {
        try {
            ApiService.setLoading(true, 'Loading scenario configuration...');
            this.currentScenario = await ApiService.getScenarioById(scenarioId);

            // Update top select value
            const topSelect = document.getElementById('top-scenario-select');
            if (topSelect) topSelect.value = scenarioId;

            this.updateHeaderStats();

            // Run fresh simulation for this scenario
            this.latestSimulation = await ApiService.runSimulation({ scenarioId });

            this.updateViewerData();
            this.renderScenarioConfigTable();
            this.renderDashboardMetrics();
            this.renderRouteAnalysisTable();

            if (this.currentView === 'risk') {
                const risk = await ApiService.getRiskAnalysis(scenarioId);
                RiskAnalysisModule.initCharts(risk);
            } else if (this.currentView === 'reports') {
                await ReportModule.loadReport(scenarioId);
            }

            this.showToast(`Loaded scenario: ${this.currentScenario.name}`, 'info');
        } catch (error) {
            console.error('Error switching scenario:', error);
            this.showToast('Failed to switch scenario: ' + error.message, 'danger');
        } finally {
            ApiService.setLoading(false);
        }
    },

    async selectScenarioAndSimulate(scenarioId) {
        await this.selectScenario(scenarioId);
        this.navigateTo('simulation');
        this.runSimulation();
    },

    // --------------------------------------------------------------------------
    // NAVIGATION / VIEW SWITCHING (10 PAGES)
    // --------------------------------------------------------------------------
    navigateTo(viewName) {
        this.currentView = viewName;
        window.location.hash = viewName;

        // Update sidebar active class
        document.querySelectorAll('.sidebar-item').forEach(item => {
            if (item.getAttribute('data-view') === viewName) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });

        // Hide all views, display current view
        document.querySelectorAll('.view-page').forEach(page => {
            page.style.display = 'none';
        });

        const activePage = document.getElementById(`view-${viewName}`);
        if (activePage) {
            activePage.style.display = 'block';
        }

        // View-specific initializations
        switch (viewName) {
            case 'dashboard':
                this.renderDashboardMetrics();
                setTimeout(() => {
                    if (this.dashboardViewer) {
                        this.dashboardViewer.resize();
                        this.updateViewerData();
                    }
                }, 100);
                break;
            case 'floorplan':
                setTimeout(() => {
                    if (this.floorPlanViewer) {
                        this.floorPlanViewer.resize();
                        this.updateViewerData();
                    }
                }, 100);
                break;
            case 'scenarios':
                this.renderScenarioConfigTable();
                break;
            case 'simulation':
                setTimeout(() => {
                    if (this.simViewer) {
                        this.simViewer.resize();
                        this.updateViewerData();
                    }
                    if (this.latestSimulation) {
                        this.simulationEngine.initSimulation(this.latestSimulation);
                    }
                }, 100);
                break;
            case 'routes':
                this.renderRouteAnalysisTable();
                break;
            case 'risk':
                if (this.currentScenario) {
                    ApiService.getRiskAnalysis(this.currentScenario.id).then(data => {
                        RiskAnalysisModule.initCharts(data);
                    });
                }
                break;
            case 'comparison':
                ScenarioComparisonModule.loadComparison();
                break;
            case 'reports':
                if (this.currentScenario) {
                    ReportModule.loadReport(this.currentScenario.id);
                }
                break;
        }

        window.scrollTo({ top: 0, behavior: 'smooth' });
    },

    // --------------------------------------------------------------------------
    // DASHBOARD RENDERING
    // --------------------------------------------------------------------------
    renderDashboardMetrics() {
        if (!this.latestSimulation) return;

        const timeElem = document.getElementById('dash-stat-time');
        if (timeElem) timeElem.innerText = `${this.latestSimulation.totalEvacuationTime}s`;

        const riskElem = document.getElementById('dash-stat-risk');
        if (riskElem) {
            riskElem.innerText = this.latestSimulation.overallRiskLevel;
            riskElem.className = 'stat-number ' + (this.latestSimulation.overallRiskLevel.includes('HIGH') ? 'text-danger' : (this.latestSimulation.overallRiskLevel.includes('MOD') ? 'text-warning' : 'text-success'));
        }

        const safeElem = document.getElementById('dash-stat-safe');
        if (safeElem) safeElem.innerText = `${this.latestSimulation.safelyEvacuated}`;

        const trappedElem = document.getElementById('dash-stat-trapped');
        if (trappedElem) {
            trappedElem.innerText = `${this.latestSimulation.trappedOccupants}`;
            trappedElem.className = this.latestSimulation.trappedOccupants > 0 ? 'text-danger fw-bold' : 'text-success';
        }

        const blockedPassagesCount = this.currentScenario && this.currentScenario.blockedPassages ? this.currentScenario.blockedPassages.length : 0;
        const blockedElem = document.getElementById('dash-stat-blocked');
        if (blockedElem) blockedElem.innerText = `${blockedPassagesCount}`;
    },

    // --------------------------------------------------------------------------
    // SIMULATION CONTROLS
    // --------------------------------------------------------------------------
    async runSimulation() {
        try {
            ApiService.setLoading(true, 'Calculating evacuation graph & crowd dynamics...');
            this.latestSimulation = await ApiService.runSimulation({ scenarioId: this.currentScenario.id });
            this.updateViewerData();
            this.renderDashboardMetrics();
            this.renderRouteAnalysisTable();

            if (this.simulationEngine) {
                this.simulationEngine.initSimulation(this.latestSimulation);
                this.simulationEngine.play();
            }
        } catch (error) {
            this.showToast('Simulation error: ' + error.message, 'danger');
        } finally {
            ApiService.setLoading(false);
        }
    },

    pauseSimulation() {
        if (this.simulationEngine) this.simulationEngine.pause();
    },

    resumeSimulation() {
        if (this.simulationEngine) this.simulationEngine.play();
    },

    resetSimulation() {
        if (this.simulationEngine) this.simulationEngine.reset();
    },

    stepSimulation() {
        if (this.simulationEngine) this.simulationEngine.stepForward(1.5);
    },

    setSimulationSpeed(speed) {
        if (this.simulationEngine) this.simulationEngine.setSpeed(speed);
        document.querySelectorAll('.btn-sim-speed').forEach(b => b.classList.remove('active'));
        const btn = document.getElementById(`btn-speed-${speed}x`);
        if (btn) btn.classList.add('active');
    },

    // --------------------------------------------------------------------------
    // SCENARIO CONFIGURATION
    // --------------------------------------------------------------------------
    renderScenarioConfigTable() {
        const floor = this.building && this.building.floors && this.building.floors[0] ? this.building.floors[0] : null;
        if (!floor) return;

        const corridorTbody = document.getElementById('config-corridors-tbody');
        if (corridorTbody) {
            corridorTbody.innerHTML = '';
            floor.corridors.forEach(c => {
                const isBlocked = this.currentScenario && this.currentScenario.blockedPassages
                        ? this.currentScenario.blockedPassages.some(bp => bp.corridorId === c.id)
                        : false;

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${c.corridorCode}</strong></td>
                    <td>${c.name}</td>
                    <td>${c.widthMeters}m</td>
                    <td>${c.maxCapacity}</td>
                    <td>
                        <span class="badge ${isBlocked ? 'bg-danger' : 'bg-success'}">${isBlocked ? 'BLOCKED' : 'CLEAR'}</span>
                    </td>
                    <td>
                        <button class="btn btn-sm ${isBlocked ? 'btn-outline-success' : 'btn-outline-danger'}" onclick="App.toggleCorridorBlock(${c.id})">
                            ${isBlocked ? '<i class="bi bi-unlock"></i> Unblock' : '<i class="bi bi-shield-x"></i> Block'}
                        </button>
                    </td>
                `;
                corridorTbody.appendChild(tr);
            });
        }

        const exitTbody = document.getElementById('config-exits-tbody');
        if (exitTbody) {
            exitTbody.innerHTML = '';
            floor.exits.forEach(e => {
                const isUnavailable = this.currentScenario && this.currentScenario.blockedPassages
                        ? this.currentScenario.blockedPassages.some(bp => bp.exitId === e.id)
                        : false;

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${e.exitCode}</strong></td>
                    <td>${e.name}</td>
                    <td>${e.exitType}</td>
                    <td>${e.flowRateCapacity} pers/min</td>
                    <td>
                        <span class="badge ${isUnavailable ? 'bg-danger' : 'bg-success'}">${isUnavailable ? 'UNAVAILABLE' : 'OPEN'}</span>
                    </td>
                    <td>
                        <button class="btn btn-sm ${isUnavailable ? 'btn-outline-success' : 'btn-outline-danger'}" onclick="App.toggleExitAvailability(${e.id})">
                            ${isUnavailable ? '<i class="bi bi-door-open"></i> Enable' : '<i class="bi bi-door-closed"></i> Disable'}
                        </button>
                    </td>
                `;
                exitTbody.appendChild(tr);
            });
        }
    },

    async toggleCorridorBlock(corridorId) {
        if (!this.currentScenario) return;
        try {
            ApiService.setLoading(true, 'Updating corridor status...');
            const updatedScenario = await ApiService.toggleScenarioCorridor(this.currentScenario.id, corridorId, 'Manual toggle via interface');
            this.currentScenario = updatedScenario;
            this.latestSimulation = await ApiService.runSimulation({ scenarioId: this.currentScenario.id });
            this.updateViewerData();
            this.renderScenarioConfigTable();
            this.renderDashboardMetrics();
            this.showToast('Corridor blockage updated successfully.', 'success');
        } catch (error) {
            this.showToast('Failed to toggle corridor: ' + error.message, 'danger');
        } finally {
            ApiService.setLoading(false);
        }
    },

    async toggleExitAvailability(exitId) {
        if (!this.currentScenario) return;
        try {
            ApiService.setLoading(true, 'Updating exit availability...');
            const updatedScenario = await ApiService.toggleScenarioExit(this.currentScenario.id, exitId, 'Manual toggle via interface');
            this.currentScenario = updatedScenario;
            this.latestSimulation = await ApiService.runSimulation({ scenarioId: this.currentScenario.id });
            this.updateViewerData();
            this.renderScenarioConfigTable();
            this.renderDashboardMetrics();
            this.showToast('Exit availability updated successfully.', 'success');
        } catch (error) {
            this.showToast('Failed to toggle exit: ' + error.message, 'danger');
        } finally {
            ApiService.setLoading(false);
        }
    },

    async handleCreateScenario(e) {
        e.preventDefault();
        const name = document.getElementById('new-scenario-name').value;
        const description = document.getElementById('new-scenario-desc').value;
        const walkingSpeed = parseFloat(document.getElementById('new-scenario-speed').value);
        const density = parseFloat(document.getElementById('new-scenario-density').value);
        const reactionDelay = parseInt(document.getElementById('new-scenario-delay').value);

        if (!name) {
            this.showToast('Please provide a scenario name.', 'warning');
            return;
        }

        try {
            ApiService.setLoading(true, 'Creating scenario...');
            const newSc = await ApiService.createScenario({
                name,
                description,
                scenarioType: 'CUSTOM',
                walkingSpeed,
                crowdDensityMultiplier: density,
                reactionDelaySeconds: reactionDelay,
                isDefault: false
            });

            this.scenarios.push(newSc);
            this.populateScenarioDropdowns();
            await this.selectScenario(newSc.id);
            this.showToast('New scenario created and activated!', 'success');

            // Close modal if present
            const modalEl = document.getElementById('createScenarioModal');
            if (modalEl) {
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
            }
        } catch (error) {
            this.showToast('Failed to create scenario: ' + error.message, 'danger');
        } finally {
            ApiService.setLoading(false);
        }
    },

    // --------------------------------------------------------------------------
    // ROUTE ANALYSIS RENDERING
    // --------------------------------------------------------------------------
    renderRouteAnalysisTable() {
        const tbody = document.getElementById('route-analysis-tbody');
        if (!tbody || !this.latestSimulation || !this.latestSimulation.routes) return;

        tbody.innerHTML = '';
        this.latestSimulation.routes.forEach(r => {
            const tr = document.createElement('tr');
            const isTrapped = !r.isReachable;
            const pathStr = (r.pathNodes && r.pathNodes.length > 0) ? r.pathNodes.join(' ➔ ') : 'PATH OBSTRUCTED';

            tr.innerHTML = `
                <td><strong>${r.roomCode}</strong></td>
                <td>${r.roomName}</td>
                <td><span class="badge bg-secondary">${r.occupantCount} pers</span></td>
                <td>${isTrapped ? '<strong class="text-danger">NO PATH (TRAPPED)</strong>' : `<strong class="text-success">${r.exitName}</strong>`}</td>
                <td><small class="text-cyan font-monospace">${pathStr}</small></td>
                <td>${r.totalDistanceMeters}m</td>
                <td><strong class="text-warning">${r.estimatedTimeSeconds}s</strong></td>
                <td><span class="badge ${isTrapped ? 'badge-risk-high' : (r.riskLevel === 'HIGH' ? 'badge-risk-high' : (r.riskLevel === 'MODERATE' ? 'badge-risk-moderate' : 'badge-risk-low'))}">${isTrapped ? 'TRAPPED' : r.riskLevel}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-info" onclick="App.inspectRoom('${r.roomCode}')">
                        <i class="bi bi-eye"></i> View
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    },

    inspectRoom(roomCode) {
        const floor = this.building && this.building.floors && this.building.floors[0] ? this.building.floors[0] : null;
        if (!floor) return;
        const room = floor.rooms.find(r => r.roomCode === roomCode);
        if (room && this.floorPlanViewer) {
            this.floorPlanViewer.activeRoomId = room.id;
            this.floorPlanViewer.render();
            this.displayRoomDetails(room);
            this.navigateTo('floorplan');
        }
    },

    displayRoomDetails(room) {
        const card = document.getElementById('room-inspector-card');
        if (!card) return;

        if (!room) {
            card.style.display = 'none';
            return;
        }

        card.style.display = 'block';
        document.getElementById('inspect-room-code').innerText = room.roomCode;
        document.getElementById('inspect-room-name').innerText = room.name;
        document.getElementById('inspect-room-type').innerText = room.roomType;
        document.getElementById('inspect-room-occupants').innerText = room.defaultOccupants;
        document.getElementById('inspect-room-capacity').innerText = room.capacity;

        if (this.latestSimulation && this.latestSimulation.routes) {
            const route = this.latestSimulation.routes.find(r => r.roomId === room.id);
            if (route) {
                document.getElementById('inspect-room-exit').innerText = route.exitName || 'None';
                document.getElementById('inspect-room-distance').innerText = `${route.totalDistanceMeters}m`;
                document.getElementById('inspect-room-time').innerText = `${route.estimatedTimeSeconds}s`;
                document.getElementById('inspect-room-path').innerText = route.pathNodes ? route.pathNodes.join(' ➔ ') : 'None';
            }
        }
    },

    // --------------------------------------------------------------------------
    // AUTHENTICATION / ROLE ACCESS PORTAL
    // --------------------------------------------------------------------------
    switchUserRole(role, name, avatar) {
        this.currentUser = { role, name, avatar };
        const label = document.getElementById('current-user-name');
        if (label) label.innerText = `${avatar} ${name} (${role})`;
        this.showToast(`Switched active profile to ${name} (${role})`, 'success');
        this.navigateTo('dashboard');
    },

    // --------------------------------------------------------------------------
    // TOAST NOTIFICATIONS
    // --------------------------------------------------------------------------
    showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type === 'danger' ? 'danger' : (type === 'warning' ? 'warning text-dark' : (type === 'success' ? 'success' : 'dark'))} border-0 show mb-2`;
        toast.setAttribute('role', 'alert');
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body small">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 4000);
    }
};

window.App = App;

// Bootstrap application on page load
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});
