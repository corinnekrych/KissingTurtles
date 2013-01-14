// Library to display the maze and its movements
//
// Code example: <pre><code>
// window.onload = function () {
//   window.ktDraw(document.getElementById('canvas'), {
//     grid: 15,
//     gridLineWidth: 5,
//     animationSpeed: 3,
//     stepDuration: 1000,
//     images: {
//       flankin: 'turtle.png',
//       emily: 'turtle.png',
//       tree1: 'tree.png'
//     }
//   }, {
//     flankin: { x: 1, y: 0, direction: '+x' }
//   })({
//     flankin: { x: 2, y: 0, direction: '+x' }
//   })({
//     emily: { x: 2, y: 1, direction: '+x' },
//     flankin: { x: 3, y: 1, direction: '+x' }
//   })({
//     emily: { x: 2, y: 1, direction: '+y' },
//     flankin: { x: 4, y: 1, direction: '+x' }
//   })({
//     emily: { x: 2, y: 2, direction: '+y' },
//     flankin: { x: 5, y: 1, direction: '+x' }
//   });
// };
// </code></pre>

(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define(factory);
  } else {
    root.ktDraw = factory();
  }
}(this, function () {
  // Polyfill for requestAnimationFrame
  var requestAnimationFrame = window.requestAnimationFrame ||
    window.mozRequestAnimationFrame ||
    window.webkitRequestAnimationFrame ||
    window.msRequestAnimationFrame ||
    function (cb) { return setTimeout(function () { cb(Date.now()); }, 1000/60); };

  // Drawing tools
  function getRotationAngle(direction) {
    switch (direction) {
      case '+x':
        return 0;
      case '-x':
        return Math.PI;
      case '+y':
        return 3 * Math.PI / 2;
      case '-y':
        return Math.PI / 2;
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
  function computeProgress(from, to, progress) {
    return from + (to - from) * progress;
  }

  /**
   * Configuration function.
   *
   * Example configuration: <pre><code>
   * {
   *   grid: 15,
   *   gridLineWidth: 5,
   *   animationSpeed: 3,
   *   stepDuration: 1000,
   *   images: {
   *     flankin: 'turtle.png',
   *     emily: 'turtle.png',
   *     tree1: 'tree.png'
   *   }
   * }
   * </code></pre>
   *
   * @param canvas the canvas element, example <code>$('#canvas')</code>.
   * @param config configuration to use.
   * @param initial the initial frame to display (see return function for format).
   * @return the animation function.
   */
  return function (canvas, config, initial) {
    var ctx = canvas.getContext('2d');
    var width = canvas.width;
    var height = canvas.height;
    var next = [];
    var wstep = width / (config.grid + 1);
    var hstep = height / (config.grid + 1);
    var current = initial;//TODO clone

    // Launch images loading in parallel
    var images = {};
    var fetchImages = function (imgs) {
      for (var name in imgs) {
        if (imgs.hasOwnProperty(name)) {
          images[name] = new Image();
          images[name].src = 'images/game/' + imgs[name];
        }
      }
    };
    fetchImages(config.images);

    // Drawing
    var clean = function () {
      ctx.clearRect(0, 0, width, height);
    };
    var drawGrid = function () {
      ctx.save();
      var wstart = Math.floor(wstep / 2);
      var hstart = Math.floor(hstep / 2);
      // Styling
      ctx.lineWidth = config.gridLineWidth;
      ctx.lineCap = 'round';
      ctx.strokeStyle = 'rgba(162, 141, 199, 0.8)';
      ctx.beginPath();
      for (var i = 1; i <= config.grid; i++) {
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
    var drawImage = function (name, x, y, rotation) {
      var half = Math.floor(wstep / 2);
      ctx.save();
      ctx.translate((x + 1) * wstep, (config.grid - y) * hstep);
      ctx.rotate(rotation);
      ctx.drawImage(images[name], -half, -half, wstep, wstep);
      ctx.restore();
    };
    var draw = function (from, to, progress) {
      var item;
      var name;
      var toitem;
      var fromAngle;
      var toAngle;
      clean();
      drawGrid();
      for (name in current) {
        item = current[name];
        drawImage(name, item.x, item.y, getRotationAngle(item.direction));
      }
      for (name in from) {
        item = from[name];
        toitem = to[name];
        fromAngle = getRotationAngle(item.direction);
        toAngle = getRotationAngle(toitem.direction);
        if (fromAngle - toAngle > Math.PI) {
          toAngle += Math.PI * 2;
        } else if (toAngle - fromAngle > Math.PI) {
          fromAngle += Math.PI * 2;
        }
        drawImage(
          name,
          computeProgress(item.x, toitem.x, progress),
          computeProgress(item.y, toitem.y, progress),
          computeProgress(fromAngle, toAngle, progress)
        );
      }
    };

    // Animate from frame to frame
    var animate = function () {
      var callback = next[0].callback;
      var frame = next[0].frame;
      var from = {};
      var to = {};
      var needAnimation = false;
      for (var name in frame) {
        if (frame.hasOwnProperty(name)) {
          if (current[name]) {
            needAnimation = true;
            from[name] = current[name];
            to[name] = frame[name];
            delete current[name];
          } else {
            current[name] = frame[name];
          }
        }
      }
      var end = Date.now() + config.stepDuration;
      var iterate = function (timestamp) {
        if ((!needAnimation) || (timestamp > end)) {
          draw(from, to, 1);
          if (callback) {
            setTimeout(callback, 0);
          }
          for (var t in to) {
            if (to.hasOwnProperty(t)) {
              current[t] = to[t];
            }
          }
          next = next.slice(1);
          if (next.length > 0) {
            setTimeout(animate, 0);
          }
        } else {
          draw(from, to, 1 - ((end - timestamp) / config.stepDuration));
          requestAnimationFrame(iterate);
        }
      };
      iterate();
    };

    // Draw initial frame
    draw({}, {}, 1);

    /**
     * Animation function.
     *
     * Example frame: <pre><code>
     * {
     *   flankin: { x: 1, y: 0, direction: '+x' },
     *   emily: { x: 3, y: 4, direction: '-y' }
     * }
     * </code></pre>
     *
     * Notes: positions are 0-indexed and y is the opposite of the US convention: 0 is bottom and grid size is top.
     *
     * @param frame the frame to draw (only differences from the previous frame are to be included).
     * @param cb callback called once animation is over.
     */
    var oneMoreStep = function (frame, callback) {
      //TODO clone frame
      next.push({ callback: callback, frame: frame });
      if (next.length === 1) {
        animate();
      }
      return oneMoreStep;
    };
    /**
     * Winning animation
     *
     * @param x x position to start the animation at.
     * @param y y position to start the animation at.
     */
    oneMoreStep.win = function (x, y, callback) {
      var dist;
      fetchImages({
        'winningHeart1': 'heart.png',
        'winningHeart2': 'heart.png',
        'winningHeart3': 'heart.png',
        'winningHeart4': 'heart.png'
      });
      var dirs = ['+x', '-x', '+y', '-y'];
      var max = Math.ceil((Math.max(Math.max(config.grid - x, x), Math.max(config.grid - y, y)) / config.animationSpeed) + 2);
      for (var i = 0; i < max - 1; i++) {
        dist = i * config.animationSpeed;
        oneMoreStep({
          winningHeart1: { x: x + dist, y: y       , direction: dirs[0] },
          winningHeart2: { x: x - dist, y: y       , direction: dirs[1] },
          winningHeart3: { x: x       , y: y + dist, direction: dirs[2] },
          winningHeart4: { x: x       , y: y - dist, direction: dirs[3] }
        });
        for (var j = 0; j < dirs.length; j++) {
          dirs[j] = nextDir(dirs[j]);
        }
      }
      dist = (max - 1) * config.animationSpeed;
      oneMoreStep({
        winningHeart1: { x: x + dist, y: y       , direction: dirs[0] },
        winningHeart2: { x: x - dist, y: y       , direction: dirs[1] },
        winningHeart3: { x: x       , y: y + dist, direction: dirs[2] },
        winningHeart4: { x: x       , y: y - dist, direction: dirs[3] }
      }, callback);
    };

    return oneMoreStep;
  };
}));
