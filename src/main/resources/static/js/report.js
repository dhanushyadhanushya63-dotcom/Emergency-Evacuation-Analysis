/**
 * Simulation Report Generator & PDF Exporter
 * Emergency Evacuation Route Simulation and Risk Analysis
 */

const ReportModule = {
    currentReportData: null,

    async loadReport(scenarioId) {
        try {
            ApiService.setLoading(true, 'Compiling comprehensive simulation report...');
            const data = await ApiService.getReport(scenarioId);
            this.currentReportData = data;
            this.renderReport(data);
        } catch (error) {
            console.error('Failed to generate report:', error);
        } finally {
            ApiService.setLoading(false);
        }
    },

    renderReport(data) {
        if (!data) return;

        // Meta
        const reportIdElem = document.getElementById('report-id-text');
        if (reportIdElem) reportIdElem.innerText = data.reportId;

        const dateElem = document.getElementById('report-timestamp');
        if (dateElem) dateElem.innerText = new Date(data.generatedAt).toLocaleString();

        const bldElem = document.getElementById('report-building-name');
        if (bldElem && data.building) bldElem.innerText = `${data.building.name} (${data.building.code})`;

        const scElem = document.getElementById('report-scenario-name');
        if (scElem && data.scenario) scElem.innerText = data.scenario.name;

        // Executive Summary
        const summaryElem = document.getElementById('report-executive-summary');
        if (summaryElem) summaryElem.innerText = data.executiveSummary;

        // Simulation Stats
        if (data.simulation) {
            const timeElem = document.getElementById('report-stat-time');
            if (timeElem) timeElem.innerText = `${data.simulation.totalEvacuationTime}s`;

            const occElem = document.getElementById('report-stat-occupants');
            if (occElem) occElem.innerText = `${data.simulation.totalOccupants}`;

            const evacElem = document.getElementById('report-stat-evacuated');
            if (evacElem) evacElem.innerText = `${data.simulation.safelyEvacuated}`;

            const trappedElem = document.getElementById('report-stat-trapped');
            if (trappedElem) trappedElem.innerText = `${data.simulation.trappedOccupants}`;

            const riskElem = document.getElementById('report-stat-risk');
            if (riskElem) riskElem.innerText = `${data.simulation.overallRiskLevel}`;
        }

        // Blocked Passages List
        const blockedList = document.getElementById('report-blocked-passages');
        if (blockedList) {
            blockedList.innerHTML = '';
            if (!data.blockedPassages || data.blockedPassages.length === 0) {
                blockedList.innerHTML = '<div class="text-secondary small">No blocked passages in this scenario.</div>';
            } else {
                data.blockedPassages.forEach(bp => {
                    const item = document.createElement('div');
                    item.className = 'p-2 mb-1 bg-dark rounded border border-danger small text-light';
                    const target = bp.corridorName ? `Corridor: ${bp.corridorName} (${bp.corridorCode})` : `Exit: ${bp.exitName} (${bp.exitCode})`;
                    item.innerHTML = `<strong class="text-danger">[BLOCKED]</strong> ${target} — <em>${bp.reason || 'Restricted passage'}</em>`;
                    blockedList.appendChild(item);
                });
            }
        }

        // Route Table
        const routeTableBody = document.getElementById('report-routes-tbody');
        if (routeTableBody && data.evacuationRoutes) {
            routeTableBody.innerHTML = '';
            data.evacuationRoutes.forEach(r => {
                const tr = document.createElement('tr');
                const isTrapped = !r.isReachable;
                const pathStr = (r.pathNodes && r.pathNodes.length > 0) ? r.pathNodes.join(' ➔ ') : 'PATH BLOCKED';

                tr.innerHTML = `
                    <td><strong>${r.roomCode}</strong></td>
                    <td>${r.roomName}</td>
                    <td>${r.occupantCount}</td>
                    <td>${isTrapped ? '<span class="text-danger fw-bold">NONE (BLOCKED)</span>' : r.exitName}</td>
                    <td><small class="text-muted font-monospace">${pathStr}</small></td>
                    <td>${r.totalDistanceMeters}m</td>
                    <td><strong>${r.estimatedTimeSeconds}s</strong></td>
                    <td><span class="badge ${isTrapped ? 'bg-danger' : (r.riskLevel === 'HIGH' ? 'bg-danger' : (r.riskLevel === 'MODERATE' ? 'bg-warning text-dark' : 'bg-success'))}">${isTrapped ? 'TRAPPED' : r.riskLevel}</span></td>
                `;
                routeTableBody.appendChild(tr);
            });
        }

        // Recommendations List
        const recList = document.getElementById('report-recommendations');
        if (recList && data.strategicRecommendations) {
            recList.innerHTML = '';
            data.strategicRecommendations.forEach(rec => {
                const li = document.createElement('li');
                li.className = 'list-group-item bg-transparent text-light border-secondary small';
                li.innerHTML = `<i class="bi bi-arrow-right-circle text-cyan me-2"></i>${rec}`;
                recList.appendChild(li);
            });
        }
    },

    exportToPdf() {
        // Trigger browser print to PDF
        window.print();
    }
};

window.ReportModule = ReportModule;
