
(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(factory);
    } else {
        root.ktDrawWalls = factory();
    }
}(this, function () {
    var pixels = 200;

    return function (canvas, config, initial) {
        var ctx = canvas.getContext('2d');
        var width = canvas.width;
        var height = canvas.height;
        var wstep = width / (config.grid + 1);
        var hstep = height / (config.grid + 1);
        var current = initial;

        // Drawing
        var clean = function () {
            ctx.clearRect(0, 0, width, height);
        };

        function computeColor(color, intensity) {
            return "rgb(" + computeIntensity(color, 0, intensity) + ", " + computeIntensity(color, 1, intensity) + ", " + computeIntensity(color, 2, intensity) + ")";
        }

        function computeIntensity(color, idx, intensity) {
            return Math.floor(color.min[idx] + ((color.max[idx] - color.min[idx]) * intensity));
        }
        var drawWall = function (x, y, rotation) {
            var green = {
                min: [55, 90, 36],
                max: [105, 170, 70]
            };
            var grid = 15;
            var centerx = (x + 1) * pixels / (grid + 1);
            var centery = (y + 1) * pixels / (grid + 1);
            var size = 5;
            for (var i = 0; i < pixels; i++) {
                for (var j = 0; j < pixels; j++) {
                    var distance = Math.sqrt((i - centerx) * (i - centerx) + (j - centery) * (j - centery));
                    if (distance < size || (distance < 2 * size && Math.random() > (distance - size) / size)) {
                        var intensity = Math.random();
                        ctx.fillStyle = computeColor(green, intensity);
                        ctx.fillRect (i, j, 1, 1);
                    }
                }
            }


        };

        var drawSimpleWall = function (x, y, rotation) {
            ctx.save();
            ctx.translate((x + 1) * wstep, (config.grid - y) * hstep);
            ctx.rotate(rotation);
            ctx.strokeStyle = 'green';
            ctx.fillRect(-wstep/2, -hstep/2, wstep, hstep);
            ctx.restore();
        };

        // Animate from frame to frame
        var animate = function () {
            var name;
            var item;
            clean();
            ctx.save();
            ctx.scale(canvas.width / pixels, canvas.height / pixels);

            for (name in current) {
                if (current.hasOwnProperty(name)) {
                    item = current[name];
                    drawWall(item.x, item.y, 0);
                }
            }

            ctx.restore();
        };

        // Draw initial frame
        animate();

        var animateSimple = function () {
            var name;
            var item;
            clean();

            for (name in current) {
                if (current.hasOwnProperty(name)) {
                    item = current[name];
                    drawSimpleWall(item.x, item.y, 0);
                }
            }
        };
        //animateSimple();

    };
}));
