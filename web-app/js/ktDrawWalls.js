// Library to display the maze and its movements
//
// Code example: <pre><code>
// window.onload = function () {
//   window.ktDraw(document.getElementById('canvas'), {
//     grid: 15,
//     gridLineWidth: 5,
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
    root.ktDrawWalls = factory();
  }
}(this, function () {

  return function (canvas, config, initial) {
    var ctx = canvas.getContext('2d');
    var width = canvas.width;
    var height = canvas.height;
    var wstep = width / (config.grid + 1);
    var hstep = height / (config.grid + 1);
    var current = initial;
    var paused = true;

    // Launch images loading in parallel
    var images = {};
    var src = {};
    var fetchImage = function (file) {
      src[file] = new Image();
      src[file].onload = function () {
        if (paused) {
          animate();
        }
      };
      src[file].src = 'images/game/' + file;
    };
    var fetchImages = function (imgs) {
      for (var name in imgs) {
        if (imgs.hasOwnProperty(name)) {
          if (!src[imgs[name]]) {
            fetchImage(imgs[name]);
          }
          images[name] = src[imgs[name]];
        }
      }
    };
    fetchImages(config.images);

    // Drawing
    var clean = function () {
      ctx.clearRect(0, 0, width, height);
    };

    var drawImage = function (name, x, y, rotation) {
      ctx.save();
      ctx.translate((x + 1) * wstep, (config.grid - y) * hstep);
      ctx.rotate(rotation);
      ctx.drawImage(images[name], -wstep/2, -hstep/2, wstep, hstep);
      ctx.restore();
    };

    // Animate from frame to frame
    var animate = function () {
      var name;
      var item;
      clean();

      for (name in current) {
        if (current.hasOwnProperty(name)) {
          item = current[name];
          drawImage(name, item.x, item.y, 0);//Can handle rotation too
        }
      }
    };

    // Draw initial frame
    animate();

  };
}));
