/* Library to display the maze and its movements */

(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define(factory);
  } else {
    root.ktMaze = factory();
  }
}(this, function () {
  var pixelsPerStep = 100;
  /* The public function
   *
   * {
   *   grid: 15
   * }
   */
  return function (canvas, config, onfinish) {
    canvas.setAttribute('width', config.grid * pixelsPerStep);
    canvas.setAttribute('height', config.grid * pixelsPerStep);
    var ctx = canvas.getContext('2d');
    setTimeout(onfinish, 0);
  };
}));