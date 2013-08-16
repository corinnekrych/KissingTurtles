(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(factory);
    } else {
        root.ktDrawGrid = factory();
    }
}(this, function () {

    return function (canvas, grid) {
        var ctx = canvas.getContext('2d');
        var width = canvas.width;
        var height = canvas.height;
        var wstep = width / (grid + 1);
        var hstep = height / (grid + 1);


        // Drawing
        var clean = function () {
            ctx.clearRect(0, 0, width, height);
        };
        var drawGrid = function () {
            ctx.save();
            var wstart = Math.floor(wstep / 2);
            var hstart = Math.floor(hstep / 2);
            // Styling
            ctx.lineWidth = 2;
            ctx.lineCap = 'round';
            ctx.strokeStyle = 'green';
            ctx.beginPath();
            for (var i = 1; i <= grid; i++) {
                // Horizontal
                ctx.moveTo(wstart, i * wstep);
                ctx.lineTo(width - wstart, i * wstep);
                // Vertical
                ctx.moveTo(i * hstep, hstart);
                ctx.lineTo(i * hstep, height - hstart);
            }
            ctx.stroke();
            ctx.closePath();
            ctx.restore();
        };

        clean();
        drawGrid();
    }
}));
