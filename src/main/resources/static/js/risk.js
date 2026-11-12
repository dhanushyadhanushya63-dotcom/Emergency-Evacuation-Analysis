/**
 * Risk Analysis Charts and Metrics Engine (Chart.js Integration)
 * Emergency Evacuation Route Simulation and Risk Analysis
 */

const RiskAnalysisModule = {
    corridorChart: null,
    exitChart: null,
    roomChart: null,

    initCharts(riskData) {
        if (!riskData) return;

        this.renderCorridorCongestionChart(riskData.corridorRisks || []);
        this.renderExitUtilizationChart(riskData.exitRisks || {});
        this.renderRoomClearanceChart(riskData.roomRisks || []);
        this.renderRecommendations(riskData.highRiskRecommendations || []);
    },

    renderCorridorCongestionChart(corridors) {
        const canvas = document.getElementById('corridorCongestionChart');
        if (!canvas) return;

        const labels = corridors.map(c => c.corridorCode);
        const data = corridors.map(c => c.congestionRatio);
        const colors = corridors.map(c => {
            if (c.isBlocked) return '#ef4444'; // Red blocked
            if (c.congestionRatio > 1.0) return '#ef4444'; // Red bottleneck
            if (c.congestionRatio > 0.65) return '#f59e0b'; // Amber
            return '#10b981'; // Green
        });

        if (this.corridorChart) {
            this.corridorChart.destroy();
        }

        this.corridorChart = new Chart(canvas, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Congestion Ratio (Occupants / Capacity)',
                    data: data,
                    backgroundColor: colors,
                    borderColor: colors,
                    borderWidth: 1,
                    borderRadius: 4
                }]
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
                        beginAtZero: true,
                        suggestedMax: 1.5
                    }
                },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            afterLabel: (ctx) => {
                                const c = corridors[ctx.dataIndex];
                                return `Capacity: ${c.maxCapacity} | Occupants: ${c.totalOccupantsRouted}`;
                            }
                        }
                    }
                }
            }
        });
    },

    renderExitUtilizationChart(exitRisks) {
        const canvas = document.getElementById('exitUtilizationChart');
        if (!canvas) return;

        const labels = Object.keys(exitRisks).map(k => exitRisks[k].name);
        const data = Object.keys(exitRisks).map(k => exitRisks[k].assignedEvacuees);
        const backgroundColors = ['#10b981', '#3b82f6', '#06b6d4', '#f59e0b'];

        if (this.exitChart) {
            this.exitChart.destroy();
        }

        this.exitChart = new Chart(canvas, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: backgroundColors,
                    borderWidth: 2,
                    borderColor: '#1e293b'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { color: '#94a3b8', font: { size: 10 } }
                    }
                }
            }
        });
    },

    renderRoomClearanceChart(roomRisks) {
        const canvas = document.getElementById('roomClearanceChart');
        if (!canvas) return;

        const labels = roomRisks.map(r => r.roomCode);
        const data = roomRisks.map(r => r.estimatedTimeSeconds);
        const colors = roomRisks.map(r => {
            if (r.isTrapped) return '#ef4444';
            if (r.estimatedTimeSeconds > 90) return '#ef4444';
            if (r.estimatedTimeSeconds > 45) return '#f59e0b';
            return '#06b6d4';
        });

        if (this.roomChart) {
            this.roomChart.destroy();
        }

        this.roomChart = new Chart(canvas, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Clearance Time (Seconds)',
                    data: data,
                    backgroundColor: colors,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: 'y',
                scales: {
                    x: {
                        ticks: { color: '#94a3b8' },
                        grid: { color: '#334155' },
                        beginAtZero: true
                    },
                    y: {
                        ticks: { color: '#94a3b8' },
                        grid: { color: '#334155' }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            }
        });
    },

    renderRecommendations(recs) {
        const container = document.getElementById('risk-recommendations-list');
        if (!container) return;

        container.innerHTML = '';
        if (recs.length === 0) {
            container.innerHTML = '<li class="list-group-item bg-transparent text-secondary">No critical risks identified.</li>';
            return;
        }

        recs.forEach(rec => {
            const isCritical = rec.includes('CRITICAL');
            const icon = isCritical ? 'bi-exclamation-octagon-fill text-danger' : 'bi-shield-exclamation text-warning';
            const li = document.createElement('li');
            li.className = 'list-group-item bg-transparent text-light border-secondary d-flex align-items-start gap-2 py-2';
            li.innerHTML = `<i class="bi ${icon} fs-6 mt-1 flex-shrink-0"></i><div>${rec}</div>`;
            container.appendChild(li);
        });
    }
};

window.RiskAnalysisModule = RiskAnalysisModule;
