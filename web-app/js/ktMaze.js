/* Library to display the maze and its movements */

(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define(factory);
  } else {
    root.ktMaze = factory();
  }
}(this, function () {
  var pixelsPerStep = 100;
  // Polyfill for requestAnimationFrame
  var requestAnimationFrame = window.requestAnimationFrame ||
    window.mozRequestAnimationFrame ||
    window.webkitRequestAnimationFrame ||
    window.msRequestAnimationFrame ||
    function (cb) { return setTimeout(function () {
      cb(Date.now());
    }, 1000/60); };
  // Image prefetching
  function preFetchImage(image, callback) {
    var img = new Image();
    img.onload = function () {
      callback();
    };
    img.onerror = function () {
      callback('Error loading ' + image);
    };
    img.src = 'images/game/' + image;
    return img;
  }
  function preFetchImages(images, callback) {
    var prefetch = {};
    var count = 0;
    function mayReply () {
      if (count === 0) {
        callback(null, prefetch);
      }
    }
    function decreaseOrFail (err) {
      if (err) {
        callback(err);
      } else {
        count--;
        mayReply();
      }
    }
    for (var name in images) {
      if (images.hasOwnProperty(name)) {
        count++;
        prefetch[name] = preFetchImage(images[name], decreaseOrFail);
      }
    }
    mayReply();// In case there is no image at all
  }
  // Drawing
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
    ctx.save();
    ctx.translate((x + 1) * pixelsPerStep, ( grid - y) * pixelsPerStep);
    ctx.rotate(getRotationAngle(direction));
    ctx.drawImage(image, -half, -half, pixelsPerStep, pixelsPerStep);
    ctx.restore();
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
   *   stepDuration: 1000,
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
    var pixels = (config.grid + 1) * pixelsPerStep;
    canvas.setAttribute('height', pixels);
    canvas.setAttribute('width', pixels);
    var ctx = canvas.getContext('2d');
    drawGrid(ctx, config.grid);
    preFetchImages(config.images, function (err, images) {
      var fixedObjects = {};
      var endStep = Date.now() + config.stepDuration;
      var idx = 0;
      var currentStep = config.steps[0];
      function iterate (timestamp) {
        var name;
        if (timestamp > endStep) {
          endStep = timestamp + config.stepDuration;
          idx++;
          if (idx >= config.steps.length) {
            onfinish();
            return;
          }
          for (name in currentStep) {
            if (currentStep.hasOwnProperty(name)) {
              fixedObjects[name] = currentStep[name];
            }
          }
          currentStep = config.steps[idx];
          for (name in currentStep) {
            if (currentStep.hasOwnProperty(name)) {
              delete fixedObjects[name];
            }
          }
        }
        ctx.clearRect(0, 0, pixels, pixels);
        drawGrid(ctx, config.grid);
        displayStep(ctx, images, fixedObjects, config.grid);
        displayStep(ctx, images, currentStep, config.grid);
        requestAnimationFrame(iterate);
      }
      if (err) {
        onfinish(err);
      } else {
        iterate(Date.now());
      }
    });
  };
}));