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
  function nextDir(direction) {
    switch (direction) {
      case '+x':
        return '-y';
      case '-x':
        return '+y';
      case '+y':
        return '+x';
      case '-y':
        return '-x';
      default:
        return '+x';
    }
  }
  function drawObject(ctx, image, x, y, rotation, grid) {
    var half = Math.floor(pixelsPerStep / 2);
    ctx.save();
    ctx.translate((x + 1) * pixelsPerStep, ( grid - y) * pixelsPerStep);
    ctx.rotate(rotation);
    ctx.drawImage(image, -half, -half, pixelsPerStep, pixelsPerStep);
    ctx.restore();
  }
  function computeProgress(from, to, progress) {
    return from + (to - from) * progress;
  }
  function drawMovingObject(ctx, image, from, to, grid, progress) {
    var fromAngle = getRotationAngle(from.direction);
    var toAngle = getRotationAngle(to.direction);
    if (fromAngle - toAngle > Math.PI) {
      toAngle += Math.PI * 2;
    } else if (toAngle - fromAngle > Math.PI) {
      fromAngle += Math.PI * 2;
    }
    drawObject(
        ctx,
        image,
        computeProgress(from.x, to.x, progress),
        computeProgress(from.y, to.y, progress),
        computeProgress(fromAngle, toAngle, progress),
        grid
      );
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
        drawObject(ctx, images[obj], step[obj].x, step[obj].y, getRotationAngle(step[obj].direction), grid);
      }
    }
  }
  function displayAnimatedStep(ctx, images, animatedFrom, animatedTo, grid, progress) {
    for (var obj in animatedTo) {
      if (animatedTo.hasOwnProperty(obj)) {
        drawMovingObject(ctx, images[obj], animatedFrom[obj], animatedTo[obj], grid, progress);
      }
    }
  }
  /* The public function, example:
   * ktMaze($('#canvas'), {
   *   grid: 15,
   *   stepDuration: 1000,
   *   winningAnimation: { x: 4, y: 3 },// optional: only if there is an animation
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
    if (config.winningAnimation) {
      config.images.winningHeart1 = 'heart.png';
      config.images.winningHeart2 = 'heart.png';
      config.images.winningHeart3 = 'heart.png';
      config.images.winningHeart4 = 'heart.png';
      var dirs = ['+x', '-x', '+y', '-y'];
      var max = Math.max(
          Math.max(config.grid - config.winningAnimation.x, config.winningAnimation.x),
          Math.max(config.grid - config.winningAnimation.y, config.winningAnimation.y)
        ) + 2;
      for (var i = 0; i < max; i++) {
        config.steps.push({
          winningHeart1: { x: config.winningAnimation.x + i, y: config.winningAnimation.y, direction: dirs[0] },
          winningHeart2: { x: config.winningAnimation.x - i, y: config.winningAnimation.y, direction: dirs[1] },
          winningHeart3: { x: config.winningAnimation.x, y: config.winningAnimation.y + i, direction: dirs[2] },
          winningHeart4: { x: config.winningAnimation.x, y: config.winningAnimation.y - i, direction: dirs[3] }
        });
        for (var j = 0; j < dirs.length; j++) {
          dirs[j] = nextDir(dirs[j]);
        }
      }
    }
    preFetchImages(config.images, function (err, images) {
      var name;
      var fixedObjects = {};
      var animatedObjectsFrom = {};
      var animatedObjectsTo = {};
      var endStep = Date.now() + config.stepDuration;
      var idx = 0;
      // Fix objects from first step
      for (name in config.steps[0]) {
        if (config.steps[0].hasOwnProperty(name)) {
          fixedObjects[name] = config.steps[0][name];
        }
      }
      function iterate (timestamp) {
        var name;
        if (timestamp > endStep) {
          endStep = timestamp + config.stepDuration;
          idx++;
          if (idx >= config.steps.length) {
            onfinish();
            return;
          }
          // Fix objects that were animated
          for (name in animatedObjectsTo) {
            if (animatedObjectsTo.hasOwnProperty(name)) {
              fixedObjects[name] = animatedObjectsTo[name];
            }
          }
          animatedObjectsTo = {};
          animatedObjectsFrom = {};
          // Find animated and fixed objects at next step
          for (name in config.steps[idx]) {
            if (config.steps[idx].hasOwnProperty(name)) {
              if (fixedObjects[name]) {
                // Object is now animated
                animatedObjectsTo[name] = config.steps[idx][name];
                animatedObjectsFrom[name] = fixedObjects[name];
                delete fixedObjects[name];
              } else {
                // Object just appeared
                fixedObjects[name] = config.steps[idx][name];
              }
            }
          }
        }
        ctx.clearRect(0, 0, pixels, pixels);
        drawGrid(ctx, config.grid);
        displayStep(ctx, images, fixedObjects, config.grid);
        displayAnimatedStep(ctx, images, animatedObjectsFrom, animatedObjectsTo, config.grid, 1 - ((endStep - timestamp) / config.stepDuration));
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