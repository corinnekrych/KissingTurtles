
(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(factory);
    } else {
        root.ktDrawWalls = factory();
    }
}(this, function () {

    return function (canvas, walls, gridSize) {
        var pixels = 200;
        var ctx = canvas.getContext('2d');
        var width = canvas.width;
        var height = canvas.height;
        var grid = gridSize;
        var wstep = width / (grid + 1);
        var hstep = height / (grid + 1);
        var current = walls;

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

            ctx.font = Math.max(2,Math.floor(pixels / (2 * grid)*0.6)) + 'pt arial';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            for (var i = 2; i < grid; i++) {
                ctx.fillStyle = 'black';
//                if ( (4*i/grid) < 1)  ctx.fillStyle = 'pink';
//                else if ( (4*i/grid) < 2) ctx.fillStyle = 'blue';
//                else if ( (4*i/grid) < 3) ctx.fillStyle = 'yellow';
//                else if ( (4*i/grid) < 4) ctx.fillStyle = 'red';

                ctx.fillText(i - 1, pixels / (grid + 1) * i, pixels - pixels / (grid + 1));
                ctx.fillText(i - 1, pixels / (grid + 1), pixels - pixels / (grid + 1) * i);
            }

        };

        // Animate from frame to frame
        var animate = function () {
            var name;
            var item;
            clean();
            ctx.save();
            ctx.scale(canvas.width / pixels, canvas.height / pixels);

            for (var i = 0; i < current.length; i++) {
                drawWall(current[i][0], (grid - current[i][1]) - 1, 0);
            }

            ctx.restore();
        };

        // Draw initial frame
        animate();

    };
}));
