/**
 * Particle Evacuation Movement Animator & Simulation Controller
 * Emergency Evacuation Route Simulation and Risk Analysis
 */

class SimulationEngine {
    constructor(viewer) {
        this.viewer = viewer;
        this.isRunning = false;
        this.isPaused = false;
        this.speed = 1.0;
        this.elapsedTime = 0.0; // simulated seconds
        this.totalTime = 0.0;
        this.animationFrameId = null;

        this.particles = [];
        this.routes = [];
        this.evacuatedCount = 0;
        this.totalOccupants = 0;
        this.trappedCount = 0;

        // Callbacks for UI updates
        this.onProgress = null;
        this.onComplete = null;
    }

    initSimulation(simulationData) {
        this.reset();
        if (!simulationData || !simulationData.routes) return;

        this.routes = simulationData.routes;
        this.totalTime = simulationData.totalEvacuationTime || 60.0;
        this.totalOccupants = simulationData.totalOccupants || 0;
        this.trappedCount = simulationData.trappedOccupants || 0;
        this.evacuatedCount = 0;

        this.spawnParticles();
    }

    spawnParticles() {
        this.particles = [];

        for (const route of this.routes) {
            if (!route.isReachable || !route.pathCoordinates || route.pathCoordinates.length < 2) continue;

            const count = route.occupantCount || 10;
            const path = route.pathCoordinates;

            for (let i = 0; i < count; i++) {
                // Stagger spawn delay slightly based on reaction delay
                const delay = (i * 0.35) + (Math.random() * 1.5);
                this.particles.push({
                    id: `${route.roomCode}-${i}`,
                    roomId: route.roomId,
                    roomCode: route.roomCode,
                    path: path,
                    currentSegment: 0,
                    t: 0.0, // progress along current segment (0 to 1)
                    x: path[0][0],
                    y: path[0][1],
                    delay: delay,
                    active: false,
                    finished: false,
                    speed: 0.015 + (Math.random() * 0.005) // base walking pace
                });
            }
        }
    }

    play() {
        if (this.isRunning && !this.isPaused) return;

        this.isRunning = true;
        this.isPaused = false;
        this.lastTimestamp = performance.now();
        this.loop(this.lastTimestamp);
    }

    pause() {
        this.isPaused = true;
        if (this.animationFrameId) {
            cancelAnimationFrame(this.animationFrameId);
            this.animationFrameId = null;
        }
    }

    reset() {
        this.pause();
        this.isRunning = false;
        this.isPaused = false;
        this.elapsedTime = 0.0;
        this.evacuatedCount = 0;
        this.particles = [];
        if (this.viewer) {
            this.viewer.render();
        }
        if (this.onProgress) {
            this.onProgress(0, 0, this.totalOccupants);
        }
    }

    setSpeed(speedMultiplier) {
        this.speed = speedMultiplier;
    }

    stepForward(deltaSeconds = 1.0) {
        this.update(deltaSeconds * 1000);
        this.draw();
        if (this.onProgress) {
            this.onProgress(this.elapsedTime, this.evacuatedCount, this.totalOccupants);
        }
    }

    loop(timestamp) {
        if (!this.isRunning || this.isPaused) return;

        const delta = (timestamp - this.lastTimestamp) * this.speed;
        this.lastTimestamp = timestamp;

        this.update(delta);
        this.draw();

        if (this.onProgress) {
            this.onProgress(this.elapsedTime, this.evacuatedCount, this.totalOccupants);
        }

        // Check completion
        const allFinished = this.particles.length > 0 && this.particles.every(p => p.finished);
        if (allFinished || (this.totalTime > 0 && this.elapsedTime >= this.totalTime + 5.0)) {
            this.isRunning = false;
            if (this.onComplete) {
                this.onComplete(this.elapsedTime, this.evacuatedCount);
            }
            return;
        }

        this.animationFrameId = requestAnimationFrame((t) => this.loop(t));
    }

    update(deltaMs) {
        const deltaSec = deltaMs / 1000.0;
        this.elapsedTime += deltaSec;

        let evacuated = 0;

        for (const p of this.particles) {
            if (p.finished) {
                evacuated++;
                continue;
            }

            if (this.elapsedTime < p.delay) {
                // Still waiting / reaction delay in room
                continue;
            }

            p.active = true;

            const p1 = p.path[p.currentSegment];
            const p2 = p.path[p.currentSegment + 1];

            const dx = p2[0] - p1[0];
            const dy = p2[1] - p1[1];
            const segmentDist = Math.sqrt(dx * dx + dy * dy);

            // Step progress
            const step = (p.speed * (deltaMs / 16.67) * 45) / Math.max(segmentDist, 1);
            p.t += step;

            if (p.t >= 1.0) {
                p.t = 0.0;
                p.currentSegment++;

                if (p.currentSegment >= p.path.length - 1) {
                    p.finished = true;
                    p.active = false;
                    evacuated++;
                    continue;
                }
            }

            // Interpolate position
            const currP1 = p.path[p.currentSegment];
            const currP2 = p.path[p.currentSegment + 1];
            p.x = currP1[0] + (currP2[0] - currP1[0]) * p.t;
            p.y = currP1[1] + (currP2[1] - currP1[1]) * p.t;
        }

        this.evacuatedCount = evacuated;
    }

    draw() {
        if (!this.viewer || !this.viewer.ctx) return;

        // Render base floor plan first
        this.viewer.render();

        const ctx = this.viewer.ctx;

        ctx.save();
        ctx.translate(this.viewer.offsetX, this.viewer.offsetY);
        ctx.scale(this.viewer.scale, this.viewer.scale);

        // Draw animated particles
        for (const p of this.particles) {
            if (!p.active || p.finished) continue;

            ctx.beginPath();
            ctx.arc(p.x, p.y, 4, 0, Math.PI * 2);
            ctx.fillStyle = '#f59e0b'; // Amber evacuee dot
            ctx.shadowColor = '#f59e0b';
            ctx.shadowBlur = 8;
            ctx.fill();

            ctx.lineWidth = 1.5;
            ctx.strokeStyle = '#ffffff';
            ctx.stroke();
        }

        ctx.restore();
    }
}

window.SimulationEngine = SimulationEngine;
