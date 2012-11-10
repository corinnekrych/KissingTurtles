var canvas = document.createElement('canvas');
canvas.style.width = '300px';
canvas.style.height = '300px';
document.body.appendChild(canvas);
ktMaze(canvas, {
  images: {
    flankin: 'turtle.png',
    emily: 'turtle.png',
    tree1: 'tree.png'
  },
  steps: [{
    flankin: { x: 3, y: 1, direction: '+x' },
    emily: { x: 6, y: 6, direction: '-y' },
    tree1: { x: 14, y: 14 }
  }, {
    flankin: { x: 4, y: 1, direction: '+x' }
  }, {
    flankin: { x: 5, y: 1, direction: '+x' }
  }, {
    flankin: { x: 6, y: 1, direction: '+x' }
  }, {
    flankin: { x: 7, y: 1, direction: '+x' }
  }],
  grid: 15,
  stepDuration: 1000
}, function () {
  console.log('done');
});