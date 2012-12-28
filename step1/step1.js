var render, loop, t, dt, cvs = document.querySelector('canvas'),
    ctx = cvs.getContext('2d'),
    turtle = new Image(),
    x = 0,
    requestAnimationFrame =
    window.requestAnimationFrame || 
    window.webkitRequestAnimationFrame ||
    window.mozRequestAnimationFrame || 
    window.msRequestAnimationFrame || 
    window.oRequestAnimationFrame ||
    function(cb) {
        return setTimeout(function() {
            cb(Date.now());
        }, 1000 / 60);
    };

cvs.width = 400;
cvs.height = 200;

turtle.src = 'http://images.wikia.com/frontierville/images/c/ce/Green_Turtle-icon.png';

drawObject= function() {
    // Clear the canvas to White
    ctx.fillStyle = "rgb(255,255,255)";
    ctx.fillRect(0, 0, cvs.width, cvs.height);

    // draw turtle
    ctx.save();
    // increment x to move turtle horizontally
    x += 5;
    // move cursor to canvas center
    ctx.translate(x, cvs.height / 2);
    // draw img center at cursor center
    ctx.drawImage(turtle, 0, 0);
    ctx.restore();
};

loop = function() {
    requestAnimationFrame(loop);
    drawObject();
};

loop();​