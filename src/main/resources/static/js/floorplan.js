/**
 * Interactive 2D Building Floor Plan Renderer (HTML5 Canvas)
 * Emergency Evacuation Route Simulation and Risk Analysis
 */

class FloorPlanViewer {
    constructor(canvasId) {
        this.canvas = document.getElementById(canvasId);
        if (!this.canvas) return;
        this.ctx = this.canvas.getContext('2d');

        // Viewport Transform State
        this.scale = 1.0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.isDragging = false;
        this.dragStartX = 0;
        this.dragStartY = 0;

        // Data State
        this.building = null;
        this.rooms = [];
        this.corridors = [];
        this.exits = [];
        this.blockedCorridorIds = new Set();
        this.unavailableExitIds = new Set();
        this.routes = [];
        this.activeRoomId = null;
        this.showRoutes = true;
        this.showCorridorCapacities = true;
        this.isInteractiveConfigMode = false;

        // Bind events
        this.initEvents();
        this.resize();
        window.addEventListener('resize', () => this.resize());
    }

    resize() {
        if (!this.canvas) return;
        const rect = this.canvas.parentElement.getBoundingClientRect();
        this.canvas.width = rect.width;
        this.canvas.height = rect.height || 600;
        this.fitToScreen();
    }

    initEvents() {
        this.canvas.addEventListener('mousedown', (e) => this.onMouseDown(e));
        this.canvas.addEventListener('mousemove', (e) => this.onMouseMove(e));
        this.canvas.addEventListener('mouseup', () => this.onMouseUp());
        this.canvas.addEventListener('mouseleave', () => this.onMouseUp());
        this.canvas.addEventListener('wheel', (e) => this.onWheel(e), { passive: false });
    }

    onMouseDown(e) {
        const rect = this.canvas.getBoundingClientRect();
        const clientX = e.clientX - rect.left;
        const clientY = e.clientY - rect.top;

        this.dragStartX = clientX - this.offsetX;
        this.dragStartY = clientY - this.offsetY;
        this.isDragging = true;
        this.hasDragged = false;
    }

    onMouseMove(e) {
        const rect = this.canvas.getBoundingClientRect();
        const clientX = e.clientX - rect.left;
        const clientY = e.clientY - rect.top;

        if (this.isDragging) {
            this.offsetX = clientX - this.dragStartX;
            this.offsetY = clientY - this.dragStartY;
            this.hasDragged = true;
            this.render();
        } else {
            // Hover cursor checks
            const worldPos = this.screenToWorld(clientX, clientY);
            const hoveredRoom = this.findRoomAt(worldPos.x, worldPos.y);
            const hoveredCorridor = this.findCorridorAt(worldPos.x, worldPos.y);
            const hoveredExit = this.findExitAt(worldPos.x, worldPos.y);

            if (hoveredRoom || hoveredCorridor || hoveredExit) {
                this.canvas.style.cursor = 'pointer';
            } else {
                this.canvas.style.cursor = 'grab';
            }
        }
    }

    onMouseUp(e) {
        if (!this.hasDragged && e) {
            const rect = this.canvas.getBoundingClientRect();
            const clientX = e.clientX - rect.left;
            const clientY = e.clientY - rect.top;
            const worldPos = this.screenToWorld(clientX, clientY);

            this.handleClick(worldPos.x, worldPos.y);
        }
        this.isDragging = false;
    }

    onWheel(e) {
        e.preventDefault();
        const zoomFactor = 1.1;
        const rect = this.canvas.getBoundingClientRect();
        const mouseX = e.clientX - rect.left;
        const mouseY = e.clientY - rect.top;

        const newScale = e.deltaY < 0 ? this.scale * zoomFactor : this.scale / zoomFactor;
        if (newScale < 0.4 || newScale > 3.0) return;

        this.offsetX = mouseX - (mouseX - this.offsetX) * (newScale / this.scale);
        this.offsetY = mouseY - (mouseY - this.offsetY) * (newScale / this.scale);
        this.scale = newScale;
        this.render();
    }

    screenToWorld(screenX, screenY) {
        return {
            x: (screenX - this.offsetX) / this.scale,
            y: (screenY - this.offsetY) / this.scale
        };
    }

    worldToScreen(worldX, worldY) {
        return {
            x: worldX * this.scale + this.offsetX,
            y: worldY * this.scale + this.offsetY
        };
    }

    fitToScreen() {
        const floorW = 1050;
        const floorH = 650;
        const scaleX = (this.canvas.width - 60) / floorW;
        const scaleY = (this.canvas.height - 60) / floorH;
        this.scale = Math.min(scaleX, scaleY, 1.2);
        this.offsetX = (this.canvas.width - floorW * this.scale) / 2 + 30;
        this.offsetY = (this.canvas.height - floorH * this.scale) / 2 + 25;
        this.render();
    }

    zoomIn() {
        this.scale = Math.min(this.scale * 1.25, 3.0);
        this.render();
    }

    zoomOut() {
        this.scale = Math.max(this.scale / 1.25, 0.4);
        this.render();
    }

    resetView() {
        this.fitToScreen();
    }

    setData(building, rooms, corridors, exits, blockedCorridors = [], unavailableExits = [], routes = []) {
        this.building = building;
        this.rooms = rooms || [];
        this.corridors = corridors || [];
        this.exits = exits || [];
        this.blockedCorridorIds = new Set(blockedCorridors);
        this.unavailableExitIds = new Set(unavailableExits);
        this.routes = routes || [];
        this.render();
    }

    setRoutes(routes) {
        this.routes = routes || [];
        this.render();
    }

    setBlockages(blockedCorridors, unavailableExits) {
        this.blockedCorridorIds = new Set(blockedCorridors);
        this.unavailableExitIds = new Set(unavailableExits);
        this.render();
    }

    handleClick(x, y) {
        // 1. Check Exit Click
        const exit = this.findExitAt(x, y);
        if (exit) {
            if (this.onExitClick) this.onExitClick(exit);
            return;
        }

        // 2. Check Corridor Click
        const corridor = this.findCorridorAt(x, y);
        if (corridor) {
            if (this.onCorridorClick) this.onCorridorClick(corridor);
            return;
        }

        // 3. Check Room Click
        const room = this.findRoomAt(x, y);
        if (room) {
            this.activeRoomId = (this.activeRoomId === room.id) ? null : room.id;
            if (this.onRoomClick) this.onRoomClick(room);
            this.render();
            return;
        }

        this.activeRoomId = null;
        if (this.onRoomClick) this.onRoomClick(null);
        this.render();
    }

    findRoomAt(x, y) {
        return this.rooms.find(r => x >= r.x && x <= r.x + r.width && y >= r.y && y <= r.y + r.height);
    }

    findCorridorAt(x, y) {
        const threshold = 18;
        return this.corridors.find(c => {
            const minX = Math.min(c.startX, c.endX) - threshold;
            const maxX = Math.max(c.startX, c.endX) + threshold;
            const minY = Math.min(c.startY, c.endY) - threshold;
            const maxY = Math.max(c.startY, c.endY) + threshold;
            return x >= minX && x <= maxX && y >= minY && y <= maxY;
        });
    }

    findExitAt(x, y) {
        const radius = 26;
        return this.exits.find(e => {
            const dx = x - e.x;
            const dy = y - e.y;
            return Math.sqrt(dx * dx + dy * dy) <= radius;
        });
    }

    // --------------------------------------------------------------------------
    // MAIN DRAW ROUTINE
    // --------------------------------------------------------------------------
    render() {
        if (!this.ctx) return;
        const ctx = this.ctx;
        ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        ctx.save();
        ctx.translate(this.offsetX, this.offsetY);
        ctx.scale(this.scale, this.scale);

        // 1. Draw Grid Background
        this.drawFloorGrid(ctx);

        // 2. Draw Corridors
        this.drawCorridors(ctx);

        // 3. Draw Rooms
        this.drawRooms(ctx);

        // 4. Draw Doorways & Connections
        this.drawDoorways(ctx);

        // 5. Draw Exits
        this.drawExits(ctx);

        // 6. Draw Evacuation Routes (if enabled)
        if (this.showRoutes && this.routes.length > 0) {
            this.drawEvacuationRoutes(ctx);
        }

        ctx.restore();
    }

    drawFloorGrid(ctx) {
        ctx.save();
        ctx.strokeStyle = '#1e293b';
        ctx.lineWidth = 0.5;

        for (let x = 0; x <= 1000; x += 50) {
            ctx.beginPath();
            ctx.moveTo(x, 0);
            ctx.lineTo(x, 600);
            ctx.stroke();
        }
        for (let y = 0; y <= 600; y += 50) {
            ctx.beginPath();
            ctx.moveTo(0, y);
            ctx.lineTo(1000, y);
            ctx.stroke();
        }

        // Outer Floor Boundary
        ctx.strokeStyle = '#334155';
        ctx.lineWidth = 3;
        ctx.strokeRect(10, 10, 980, 580);
        ctx.restore();
    }

    drawCorridors(ctx) {
        for (const c of this.corridors) {
            const isBlocked = this.blockedCorridorIds.has(c.id) || c.isBlocked;
            const width = Math.max((c.widthMeters || 2.5) * 12, 28);

            ctx.save();
            ctx.lineCap = 'square';
            ctx.lineWidth = width;

            if (isBlocked) {
                ctx.strokeStyle = 'rgba(239, 68, 68, 0.4)';
            } else {
                ctx.strokeStyle = 'rgba(30, 41, 59, 0.9)';
            }

            ctx.beginPath();
            ctx.moveTo(c.startX, c.startY);
            ctx.lineTo(c.endX, c.endY);
            ctx.stroke();

            // Corridor Outline Borders
            ctx.lineWidth = 1.5;
            ctx.strokeStyle = isBlocked ? '#ef4444' : '#475569';
            ctx.stroke();

            // Hazard Hatching if Blocked
            if (isBlocked) {
                this.drawHazardStripes(ctx, c.startX, c.startY, c.endX, c.endY, width);
            }

            // Corridor Label
            const midX = (c.startX + c.endX) / 2;
            const midY = (c.startY + c.endY) / 2;
            ctx.fillStyle = isBlocked ? '#fca5a5' : '#94a3b8';
            ctx.font = '10px Inter, sans-serif';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(c.corridorCode, midX, midY - 6);

            if (this.showCorridorCapacities) {
                ctx.fillStyle = isBlocked ? '#ef4444' : '#64748b';
                ctx.font = '9px Inter, sans-serif';
                ctx.fillText(isBlocked ? '[BLOCKED]' : `${c.widthMeters}m | Cap: ${c.maxCapacity}`, midX, midY + 7);
            }

            ctx.restore();
        }
    }

    drawHazardStripes(ctx, x1, y1, x2, y3, width) {
        ctx.save();
        ctx.beginPath();
        ctx.moveTo(x1, y1);
        ctx.lineTo(x2, y3);
        ctx.lineWidth = width;
        ctx.strokeStyle = '#ef4444';
        ctx.setLineDash([8, 8]);
        ctx.stroke();
        ctx.restore();
    }

    drawRooms(ctx) {
        for (const r of this.rooms) {
            const isSelected = (this.activeRoomId === r.id);

            ctx.save();
            // Background fill
            if (isSelected) {
                ctx.fillStyle = 'rgba(59, 130, 246, 0.35)';
            } else {
                ctx.fillStyle = '#0f172a';
            }
            ctx.fillRect(r.x, r.y, r.width, r.height);

            // Border
            ctx.lineWidth = isSelected ? 3 : 2;
            ctx.strokeStyle = isSelected ? '#38bdf8' : '#334155';
            ctx.strokeRect(r.x, r.y, r.width, r.height);

            // Room Code Header Pill
            ctx.fillStyle = isSelected ? '#0284c7' : '#1e293b';
            ctx.fillRect(r.x + 8, r.y + 8, 54, 18);
            ctx.fillStyle = '#fff';
            ctx.font = 'bold 10px Inter, sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText(r.roomCode, r.x + 35, r.y + 20);

            // Room Name
            ctx.fillStyle = '#f8fafc';
            ctx.font = '600 11px Inter, sans-serif';
            ctx.textAlign = 'left';
            this.wrapText(ctx, r.name, r.x + 12, r.y + 45, r.width - 24, 14);

            // Occupants Counter Badge
            ctx.fillStyle = '#f59e0b';
            ctx.font = '500 10px Inter, sans-serif';
            ctx.fillText(`👥 ${r.defaultOccupants} pers`, r.x + 12, r.y + r.height - 14);

            ctx.restore();
        }
    }

    wrapText(ctx, text, x, y, maxWidth, lineHeight) {
        const words = text.split(' ');
        let line = '';
        for (let n = 0; n < words.length; n++) {
            const testLine = line + words[n] + ' ';
            const metrics = ctx.measureText(testLine);
            if (metrics.width > maxWidth && n > 0) {
                ctx.fillText(line, x, y);
                line = words[n] + ' ';
                y += lineHeight;
            } else {
                line = testLine;
            }
        }
        ctx.fillText(line, x, y);
    }

    drawDoorways(ctx) {
        ctx.save();
        ctx.strokeStyle = '#38bdf8';
        ctx.lineWidth = 2;

        for (const r of this.rooms) {
            // Draw small door opening ticks
            const cx = r.centerX;
            const cy = r.centerY;

            ctx.beginPath();
            if (r.y < 200) {
                // North rooms: door on south wall
                ctx.arc(cx, r.y + r.height, 6, Math.PI, 0, false);
            } else if (r.y > 400) {
                // South rooms: door on north wall
                ctx.arc(cx, r.y, 6, 0, Math.PI, false);
            } else if (r.x < 300) {
                // West bay: door on east wall
                ctx.arc(r.x + r.width, cy, 6, -Math.PI / 2, Math.PI / 2, false);
            } else {
                // East bay: door on west wall
                ctx.arc(r.x, cy, 6, Math.PI / 2, -Math.PI / 2, false);
            }
            ctx.stroke();
        }
        ctx.restore();
    }

    drawExits(ctx) {
        for (const e of this.exits) {
            const isBlocked = this.unavailableExitIds.has(e.id) || !e.isAvailable;
            const isMain = (e.exitType === 'MAIN_ENTRANCE');

            ctx.save();
            ctx.beginPath();
            ctx.arc(e.x, e.y, 22, 0, Math.PI * 2);

            if (isBlocked) {
                ctx.fillStyle = 'rgba(239, 68, 68, 0.25)';
                ctx.strokeStyle = '#ef4444';
            } else if (isMain) {
                ctx.fillStyle = 'rgba(59, 130, 246, 0.25)';
                ctx.strokeStyle = '#3b82f6';
            } else {
                ctx.fillStyle = 'rgba(16, 185, 129, 0.25)';
                ctx.strokeStyle = '#10b981';
            }

            ctx.lineWidth = 3;
            ctx.fill();
            ctx.stroke();

            // Icon / Text inside Exit
            ctx.fillStyle = '#fff';
            ctx.font = 'bold 11px Inter, sans-serif';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(isBlocked ? '✕' : (isMain ? 'ENTRY' : 'EXIT'), e.x, e.y);

            // Label tag
            ctx.font = 'bold 10px Inter, sans-serif';
            ctx.fillStyle = isBlocked ? '#fca5a5' : '#f8fafc';
            ctx.fillText(e.exitCode, e.x, e.y + 32);

            ctx.restore();
        }
    }

    drawEvacuationRoutes(ctx) {
        ctx.save();
        for (const route of this.routes) {
            if (!route.isReachable || !route.pathCoordinates || route.pathCoordinates.length < 2) continue;

            const isFocusRoom = (this.activeRoomId === route.roomId);
            const isAlternative = (route.routeType === 'ALTERNATIVE');

            ctx.beginPath();
            ctx.moveTo(route.pathCoordinates[0][0], route.pathCoordinates[0][1]);
            for (let i = 1; i < route.pathCoordinates.length; i++) {
                ctx.lineTo(route.pathCoordinates[i][0], route.pathCoordinates[i][1]);
            }

            if (isFocusRoom) {
                ctx.lineWidth = 5;
                ctx.strokeStyle = '#fbbf24'; // Bright amber gold
                ctx.setLineDash([]);
                ctx.shadowColor = '#f59e0b';
                ctx.shadowBlur = 15;
            } else if (isAlternative) {
                ctx.lineWidth = 2.5;
                ctx.strokeStyle = 'rgba(168, 85, 247, 0.8)'; // Purple dashed
                ctx.setLineDash([6, 6]);
                ctx.shadowBlur = 0;
            } else {
                ctx.lineWidth = 3;
                ctx.strokeStyle = 'rgba(6, 182, 212, 0.7)'; // Cyan path
                ctx.setLineDash([]);
                ctx.shadowColor = 'rgba(6, 182, 212, 0.4)';
                ctx.shadowBlur = 8;
            }

            ctx.stroke();
        }
        ctx.restore();
    }
}

window.FloorPlanViewer = FloorPlanViewer;
