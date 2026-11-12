/**
 * Centralized REST API Service Wrapper
 * Emergency Evacuation Route Simulation and Risk Analysis
 */

const API_BASE_URL = window.location.origin + '/api';

const ApiService = {
    // Show/hide global loading spinner
    setLoading(visible, message = 'Processing simulation...') {
        const overlay = document.getElementById('loading-overlay');
        const text = document.getElementById('loading-message');
        if (overlay) {
            overlay.style.display = visible ? 'flex' : 'none';
        }
        if (text && message) {
            text.innerText = message;
        }
    },

    // Generic fetch helper with error handling
    async request(endpoint, options = {}) {
        try {
            const url = `${API_BASE_URL}${endpoint}`;
            const headers = {
                'Content-Type': 'application/json',
                ...options.headers
            };

            const response = await fetch(url, {
                ...options,
                headers
            });

            if (!response.ok) {
                let errorDetails = 'API request failed';
                try {
                    const errorJson = await response.json();
                    errorDetails = errorJson.message || errorJson.error || response.statusText;
                } catch {
                    errorDetails = await response.text() || response.statusText;
                }
                throw new Error(errorDetails);
            }

            return await response.json();
        } catch (error) {
            console.error(`API Error on ${endpoint}:`, error);
            throw error;
        }
    },

    // Building Endpoints
    async getSampleBuilding() {
        return await this.request('/buildings/sample');
    },

    async getAllBuildings() {
        return await this.request('/buildings');
    },

    // Scenario Endpoints
    async getAllScenarios() {
        return await this.request('/scenarios');
    },

    async getScenarioById(id) {
        return await this.request(`/scenarios/${id}`);
    },

    async createScenario(scenarioData) {
        return await this.request('/scenarios', {
            method: 'POST',
            body: JSON.stringify(scenarioData)
        });
    },

    async toggleScenarioCorridor(scenarioId, corridorId, reason) {
        return await this.request(`/scenarios/${scenarioId}/toggle-corridor/${corridorId}`, {
            method: 'POST',
            body: JSON.stringify({ reason })
        });
    },

    async toggleScenarioExit(scenarioId, exitId, reason) {
        return await this.request(`/scenarios/${scenarioId}/toggle-exit/${exitId}`, {
            method: 'POST',
            body: JSON.stringify({ reason })
        });
    },

    // Simulation Endpoints
    async runSimulation(payload = {}) {
        return await this.request('/simulation/run', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
    },

    async getLatestSimulation(scenarioId) {
        return await this.request(`/simulation/latest/${scenarioId}`);
    },

    // Risk Analysis Endpoints
    async getRiskAnalysis(scenarioId) {
        return await this.request(`/risk-analysis/${scenarioId}`);
    },

    // Scenario Comparison Endpoints
    async getComparison(scenarioIds = '') {
        const query = scenarioIds ? `?scenarioIds=${scenarioIds}` : '';
        return await this.request(`/comparison${query}`);
    },

    // Report Endpoints
    async getReport(scenarioId) {
        return await this.request(`/reports/${scenarioId}`);
    }
};

window.ApiService = ApiService;
