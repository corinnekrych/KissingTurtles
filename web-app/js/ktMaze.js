/* Library to display the maze and its movements */

(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define(factory);
  } else {
    root.ktMaze = factory();
  }
}(this, function () {
  var pixelsPerStep = 100;
  function drawGrid(ctx, grid) {
    var start = Math.floor(pixelsPerStep / 2);
    var end = ((grid + 1) * pixelsPerStep) - start;
    var offset;
    // Styling
    ctx.lineWidth = 5;
    ctx.lineCap = 'round';
    ctx.strokeStyle = 'rgba(162, 141, 199, 0.8)';
    ctx.beginPath();
    for (var i = 1; i <= grid; i++) {
      offset = i * pixelsPerStep;
      // Horizontal
      ctx.moveTo(start, offset);
      ctx.lineTo(end, offset);
      // Vertical
      ctx.moveTo(offset, start);
      ctx.lineTo(offset, end);
    }
    ctx.stroke();
    ctx.closePath();
  }
  /* The public function, example:
   * ktMaze($('#canvas'), {
   *   grid: 15
   * }, function () {
   *   console.log('Canvas display or animation finished');
   * });
   */
  return function (canvas, config, onfinish) {
    canvas.setAttribute('height', (config.grid + 1) * pixelsPerStep);
    canvas.setAttribute('width', (config.grid + 1) * pixelsPerStep);
    var ctx = canvas.getContext('2d');
    drawGrid(ctx, config.grid);
    setTimeout(onfinish, 0);
  };
}));