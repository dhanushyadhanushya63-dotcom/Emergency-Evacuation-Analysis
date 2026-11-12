/**
 * Scenario Comparison Controller
 * Emergency Evacuation Route Simulation and Risk Analysis
 */

const ScenarioComparisonModule = {
    comparisonChart: null,

    async loadComparison(scenarioIds = '') {
        try {
            ApiService.setLoading(true, 'Calculating multi-scenario comparative simulation...');
            const data = await ApiService.getComparison(scenarioIds);
            this.renderComparisonView(data);
        } catch (error) {
            console.error('Failed to load comparison data:', error);
        } finally {
            ApiService.setLoading(false);
        }
    },

    renderComparisonView(data) {
        if (!data || !data.scenarios) return;

        // Render Summary Banner
        const summaryText = document.getElementById('comparison-summary-text');
        if (summaryText) {
            summaryText.innerText = data.comparisonSummary || 'Comparison completed successfully.';
        }

        const bestBadge = document.getElementById('comparison-best-scenario');
        if (bestBadge) {
            bestBadge.innerText = data.mostOptimalScenario || 'N/A';
        }

        const worstBadge = document.getElementById('comparison-worst-scenario');
        if (worstBadge) {
            worstBadge.innerText = data.highestRiskScenario || 'N/A';
        }

        // Render Comparison Cards Grid
        const cardsContainer = document.getElementById('scenario-comparison-cards');
        if (cardsContainer) {
            cardsContainer.innerHTML = '';
            data.scenarios.forEach(sc => {
                const isWorst = sc.trappedOccupants > 0 || sc.peakCongestionRatio > 1.2;
                const card = document.createElement('div');
                card.className = 'col-lg-4 col-md-6 mb-3';
                card.innerHTML = `
                    <div class="comparison-card ${isWorst ? 'border-danger' : 'border-secondary'}">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="badge ${isWorst ? 'bg-danger' : 'bg-primary'}">${sc.scenarioType}</span>
                            <span class="badge ${sc.overallRiskLevel.includes('HIGH') ? 'badge-risk-high' : (sc.overallRiskLevel.includes('MOD') ? 'badge-risk-moderate' : 'badge-risk-low')}">${sc.overallRiskLevel}</span>
                        </div>
                        <h6 class="text-white fw-bold mb-3">${sc.scenarioName}</h6>
                        <div class="row g-2 mb-2 text-secondary small">
                            <div class="col-6">Clearance Time: <strong class="text-warning">${sc.totalEvacuationTime}s</strong></div>
                            <div class="col-6">Avg Room Time: <strong class="text-info">${sc.averageEvacuationTime}s</strong></div>
                            <div class="col-6">Available Exits: <strong class="text-success">${sc.availableExits}</strong></div>
                            <div class="col-6">Blocked Corridors: <strong class="text-danger">${sc.blockedCorridors}</strong></div>
                            <div class="col-6">Peak Congestion: <strong>${sc.peakCongestionRatio}</strong></div>
                            <div class="col-6">Trapped: <strong class="${sc.trappedOccupants > 0 ? 'text-danger fw-bold' : 'text-success'}">${sc.trappedOccupants}</strong></div>
                        </div>
                        <button class="btn btn-sm btn-outline-cyan w-100 mt-2" onclick="App.selectScenarioAndSimulate(${sc.scenarioId})">
                            <i class="bi bi-play-circle me-1"></i> Simulate Scenario
                        </button>
                    </div>
                `;
                cardsContainer.appendChild(card);
            });
        }

        // Render Comparison Chart
        this.renderComparisonChart(data.scenarios);
    },

    renderComparisonChart(scenarios) {
        const canvas = document.getElementById('scenarioComparisonChart');
        if (!canvas) return;

        const labels = scenarios.map(s => s.scenarioName.length > 20 ? s.scenarioName.substring(0, 18) + '...' : s.scenarioName);
        const times = scenarios.map(s => s.totalEvacuationTime);
        const congestion = scenarios.map(s => Math.round(s.peakCongestionRatio * 100));

        if (this.comparisonChart) {
            this.comparisonChart.destroy();
        }

        this.comparisonChart = new Chart(canvas, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Total Evacuation Time (s)',
                        data: times,
                        backgroundColor: 'rgba(59, 130, 246, 0.7)',
                        borderColor: '#3b82f6',
                        borderWidth: 1,
                        borderRadius: 4
                    },
                    {
                        label: 'Peak Congestion Index (%)',
                        data: congestion,
                        backgroundColor: 'rgba(239, 68, 68, 0.7)',
                        borderColor: '#ef4444',
                        borderWidth: 1,
                        borderRadius: 4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    x: {
                        ticks: { color: '#94a3b8' },
                        grid: { color: '#334155' }
                    },
                    y: {
                        ticks: { color: '#94a3b8' },
                        grid: { color: '#334155' },
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: {
                        labels: { color: '#f8fafc' }
                    }
                }
            }
        });
    }
};

window.ScenarioComparisonModule = ScenarioComparisonModule;
