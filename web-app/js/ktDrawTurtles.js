(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(factory);
    } else {
        root.ktDrawTurtles = factory();
    }
}(this, function () {
    // Polyfill for requestAnimationFrame
    var requestAnimationFrame = window.requestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        function (cb) { return setTimeout(cb, 1000/60); };

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

    return function (canvas, config, initial) {
        var ctx = canvas.getContext('2d');
        var width = canvas.width;
        var height = canvas.height;
        var wstep = width / (config.grid + 1);
        var hstep = height / (config.grid + 1);
        var current = initial;
        var animations = {};
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

            src[file].onerror = function () {
                alert("Error!!!");
            };

//            src[file].src = 'images/game/snake.png';
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
            var timestamp = Date.now();
            var name;
            var item;
            var animation;
            var animateMore = false;
            clean();
            for (name in animations) {
                if (animations.hasOwnProperty(name)) {
                    animation = animations[name];
                    if (animation.end < timestamp) {
                        current[name] = animation.to;
                        if (animation.cb) {
                            setTimeout(animation.cb, 0);
                        }
                        delete animations[name];
                    } else {
                        animateMore = true;
                        var progress = 1 - ((animation.end - timestamp) / config.stepDuration);
                        var currentx = computeProgress(animation.from.x, animation.to.x, progress);
                        var currenty = computeProgress(animation.from.y, animation.to.y, progress);
                        drawImage(name, currentx, currenty, 0);//Can handle rotation too
                    }
                }
            }
            for (name in current) {
                if (current.hasOwnProperty(name)) {
                    item = current[name];
                    drawImage(name, item.x, item.y, 0);//Can handle rotation too
                }
            }
            if (!animateMore) {
                paused = true;
            } else {
                requestAnimationFrame(animate);
            }
        };

        // Draw initial frame
        animate();

        var animateLater = function (name, to, callback) {
            var oldcb = animations[name].cb;
            var frame = {};
            frame[name] = to;
            animations[name].cb = function () {
                if (oldcb) {
                    oldcb();
                }
                oneMoreStep(frame, callback);
            };
        };

        var oneMoreStep = function (frame, callback) {
            var caller = null;
            for (var name in frame) {
                if (frame.hasOwnProperty(name)) {
                    if (current.hasOwnProperty(name)) {
                        // Currently present but not animated
                        animations[name] = {
                            from: current[name],
                            to: frame[name],
                            end: Date.now() + config.stepDuration
                        };
                        caller = animations[name];
                        delete current[name];
                    } else if (animations.hasOwnProperty(name)) {
                        // Currently animated: add the step once this one is finished
                        animateLater(name, frame[name], callback);
                        callback = null;
                        caller = {};
                    } else {
                        current[name] = frame[name];
                    }
                }
            }
            animate();
            if (caller) {
                caller.cb = callback;
                paused = false;
            } else if (callback) {
                setTimeout(callback, 0);
            }
            return oneMoreStep;
        };

        oneMoreStep.win = function (x, y, callback) {
            var dist;
            var speed = 3;
            fetchImages({
                'winningHeart1': 'heart.png',
                'winningHeart2': 'heart.png',
                'winningHeart3': 'heart.png',
                'winningHeart4': 'heart.png'
            });
            var dirs = ['+x', '-x', '+y', '-y'];
            var max = Math.ceil((Math.max(Math.max(config.grid - x, x), Math.max(config.grid - y, y)) / speed) + 2);
            var i = 0;
            var iteration = function () {
                if (i < max) {
                    dist = i * speed;
                    oneMoreStep({
                        winningHeart1: { x: x + dist, y: y       , direction: dirs[0] },
                        winningHeart2: { x: x - dist, y: y       , direction: dirs[1] },
                        winningHeart3: { x: x       , y: y + dist, direction: dirs[2] },
                        winningHeart4: { x: x       , y: y - dist, direction: dirs[3] }
                    }, iteration);
                    for (var j = 0; j < dirs.length; j++) {
                        dirs[j] = nextDir(dirs[j]);
                    }
                }
                if (i == max) {
                    if (callback) {
                        setTimeout(callback, 0);
                    }
                }
                i++;
            };
            oneMoreStep({}, iteration);
        };

        return oneMoreStep;
    };
}));
