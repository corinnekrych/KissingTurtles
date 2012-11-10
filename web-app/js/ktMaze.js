/* Library to display the maze and its movements */

(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define(factory);
  } else {
    root.ktMaze = factory();
  }
}(this, function () {
  var pixelsPerStep = 100;
  function getRotationAngle(direction) {
    switch (direction) {
      case '+x':
        return 0;
      case '-x':
        return Math.PI;
      case '+y':
        return Math.PI / 2;
      case '-y':
        return 3 * Math.PI / 2;
      default:
        return 0;
    }
  }
  function drawObject(ctx, image, x, y, direction, grid) {
    var half = Math.floor(pixelsPerStep / 2);
    var img = new Image();
    img.onload = function () {
      ctx.save();
      ctx.translate((x * pixelsPerStep) + half, (( grid - y - 1) * pixelsPerStep) + half);
      ctx.rotate(getRotationAngle(direction));
      ctx.drawImage(img, 0, 0, pixelsPerStep, pixelsPerStep);
      ctx.restore();
    };
    img.src = 'images/game/' + image;
  }
  function drawGrid(ctx, grid) {
    ctx.save();
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
    ctx.restore();
  }
  function displayStep(ctx, images, step, grid) {
    for (var obj in step) {
      if (step.hasOwnProperty(obj)) {
        drawObject(ctx, images[obj], step[obj].x, step[obj].y, step[obj].direction, grid);
      }
    }
  }
  /* The public function, example:
   * ktMaze($('#canvas'), {
   *   grid: 15,
   *   images: {
   *     flankin: 'turtle.png',
   *     emily: 'turle.png',
   *     tree1: 'tree.png'
   *   },
   *   steps: [{
   *     // initial positions
   *     flankin: { x: 1, y: 0, direction: '+x' },
   *     emily: { x: 3, y: 4, direction: '-y' },
   *     tree1: { x: 5, y: 6 }
   *   }, {
   *     // differences from previous step
   *     flankin: { x: 2, y: 0, direction: '+x' }
   *   }]
   * }, function () {
   *   console.log('Canvas display or animation finished');
   * });
   *
   * Notes:
   * # positions are 0-indexed
   * # y is the opposite of the US convention: 0 is bottom and grid size is top
   */
  return function (canvas, config, onfinish) {
    canvas.setAttribute('height', (config.grid + 1) * pixelsPerStep);
    canvas.setAttribute('width', (config.grid + 1) * pixelsPerStep);
    var ctx = canvas.getContext('2d');
    var idx = 0;
    function iterate () {
      if (idx < config.steps.length) {
        displayStep(ctx, config.images, config.steps[idx], config.grid);
        idx++;
        setTimeout(iterate, 1000);
      } else {
        onfinish();
      }
    }
    drawGrid(ctx, config.grid);
    iterate();
  };
}));