// toggle icon navbar
let menuIcon = document.querySelector('#menu-icon');
let navbar = document.querySelector('.navbar');

menuIcon.onclick = () => {
    menuIcon.classList.toggle('bx-x');
    navbar.classList.toggle('active');
}

// scroll sections
let sections = document.querySelectorAll('section');
let navLinks = document.querySelectorAll('header nav a');

window.onscroll = () => {
    sections.forEach(sec => {
        let top = window.scrollY;
        let offset = sec.offsetTop - 100;
        let height = sec.offsetHeight;
        let id = sec.getAttribute('id');

        if(top >= offset && top < offset + height) {
            // active navbar links
            navLinks.forEach(links => {
                links.classList.remove('active');
                document.querySelector('header nav a[href*=' + id + ']').classList.add('active');
            });
            // active sections for animation on scroll
            sec.classList.add('show-animate');
        }
        // if want to animation that repeats on scroll use this
        else {
            sec.classList.remove('show-animate');
        }
    });

    // sticky navbar
    let header = document.querySelector('header');

    header.classList.toggle('sticky', window.scrollY > 100);

    // remove toggle icon and navbar when click navbar links (scroll)
    menuIcon.classList.remove('bx-x');
    navbar.classList.remove('active');

    // animation footer on scroll
    let footer = document.querySelector('footer');

    footer.classList.toggle('show-animate', this.innerHeight + this.scrollY >= document.scrollingElement.scrollHeight);
}

//Macintosh FBX Model

// Three.js code for the interactive animation
var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
var renderer = new THREE.WebGLRenderer({ antialias: true });

// Set size of the renderer to match the container
var container = document.getElementById('animation-container');
renderer.setSize(container.clientWidth, container.clientHeight);

// Set the background color of the renderer to match the site background
renderer.setClearColor(0x2C2C34);
container.appendChild(renderer.domElement);

// Add lighting to the scene
var ambientLight = new THREE.AmbientLight(0x404040, 1.5); // soft white light
scene.add(ambientLight);

var directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
directionalLight.position.set(1, 1, 1).normalize();
scene.add(directionalLight);

var object;
// Load the FBX model
var loader = new THREE.FBXLoader();
loader.load('assets/macintosh-classic02.fbx', function (obj) {
    object = obj;
    object.scale.set(0.2, 0.1, 0.2); // Adjust the scale to make it smaller
    object.position.set(0, -0.2, 0); // Adjust the position if necessary
    scene.add(object);
    render(); // Call render function after adding the object
}, undefined, function (error) {
    console.error('An error happened while loading the model:', error);
});

camera.position.z = 2.5; // Move the camera further away from the object

// Add OrbitControls for interactivity
var controls = new THREE.OrbitControls(camera, renderer.domElement);
controls.enableDamping = true; // Enable damping (inertia)
controls.dampingFactor = 0.05; // Damping factor
controls.screenSpacePanning = false; // Do not allow panning
controls.maxPolarAngle = Math.PI / 2; // Limit vertical rotation

// Limit the zoom distance
controls.minDistance = 1; // Minimum distance the camera can be from the object
controls.maxDistance = 3; // Maximum distance the camera can be from the object

function animate() {
    requestAnimationFrame(animate);

    if (controls) {
        controls.update();
    }

    if (object) {
        object.rotation.y -= 0.001; // Rotate the object slowly on its own
    }

    render();
}

function render() {
    renderer.render(scene, camera);
}

animate();

window.addEventListener('resize', function () {
    var width = container.clientWidth;
    var height = container.clientHeight;
    renderer.setSize(width, height);
    camera.aspect = width / height;
    camera.updateProjectionMatrix();
});



// SKILLS SECTION
document.addEventListener('DOMContentLoaded', function() {
    const skillsSection = document.getElementById('skills');
    const skillBars = document.querySelectorAll('.skills-content .bar span');

    function isInViewport(element) {
        const rect = element.getBoundingClientRect();
        return (
            rect.top < (window.innerHeight || document.documentElement.clientHeight) &&
            rect.bottom > 0 &&
            rect.left < (window.innerWidth || document.documentElement.clientWidth) &&
            rect.right > 0
        );
    }

    function activateSkills() {
        console.log('Activating skills');
        skillBars.forEach(bar => {
            console.log('Activating bar:', bar); // Log bar being activated
            bar.classList.add('active');
        });
    }

    function resetSkills() {
        console.log('Resetting skills');
        skillBars.forEach(bar => {
            console.log('Resetting bar:', bar); // Log bar being reset
            bar.classList.remove('active');
        });
    }

    function checkSkills() {
        console.log('Checking if skills section is in viewport');
        if (isInViewport(skillsSection)) {
            console.log('Skills section is in viewport');
            activateSkills();
        } else {
            console.log('Skills section is not in viewport');
            resetSkills();
        }
    }

    window.addEventListener('scroll', checkSkills);
    window.addEventListener('resize', checkSkills); // Also check on resize
    console.log('Initial check'); // Log initial check
    checkSkills(); // Check initially in case the section is already in view
});


//PROJECTS SLIDESHOW
let slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
    showSlides(slideIndex += n);
}

function showSlides(n) {
    let i;
    let slides = document.getElementsByClassName("slide");
    if (n > slides.length) {slideIndex = 1}
    if (n < 1) {slideIndex = slides.length}
    for (i = 0; i < slides.length; i++) {
        slides[i].classList.remove("active-slide");
    }
    slides[slideIndex-1].classList.add("active-slide");
}




/// CHARACTER MODEL 

// // Three.js code for the interactive animation
// var scene = new THREE.Scene();
// var camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
// var renderer = new THREE.WebGLRenderer({ antialias: true });

// // Set size of the renderer to match the container
// var container = document.getElementById('animation-container');
// renderer.setSize(container.clientWidth, container.clientHeight);

// // Set the background color of the renderer to match the site background
// renderer.setClearColor(0x2C2C34);
// container.appendChild(renderer.domElement);

// // Add lighting to the scene
// var ambientLight = new THREE.AmbientLight(0x404040, 1); // soft white light
// scene.add(ambientLight);

// var directionalLight = new THREE.DirectionalLight(0xffffff, 0.5);
// directionalLight.position.set(1, 1, 1).normalize();
// scene.add(directionalLight);

// var object, mixer;
// // Load the FBX model
// var loader = new THREE.FBXLoader();
// loader.load('assets/BlueDemon.fbx', function (obj) {
//     object = obj;
//     object.scale.set(0.3, 0.2, 0.3); // Adjust the scale to make it fit within the viewport
//     object.position.set(0, -5, 0); // Adjust the position to center the model
//     scene.add(object);

//     // Animation mixer for the loaded object
//     mixer = new THREE.AnimationMixer(obj);
//     // Assuming the first animation in the model is the waving animation
//     var action = mixer.clipAction(obj.animations[8]);
//     action.setDuration(5); // Slow down the animation to 5 seconds
//     action.play();

//     render(); // Call render function after adding the object
// }, undefined, function (error) {
//     console.error('An error happened while loading the model:', error);
// });

// camera.position.set(0, 0, 150); // Adjust camera position to better view the model

// // Add OrbitControls for interactivity
// var controls = new THREE.OrbitControls(camera, renderer.domElement);
// controls.enableDamping = true; // Enable damping (inertia)
// controls.dampingFactor = 0.05; // Damping factor
// controls.screenSpacePanning = false; // Do not allow panning
// controls.maxPolarAngle = Math.PI / 2; // Limit vertical rotation

// function animate() {
//     requestAnimationFrame(animate);

//     if (controls) {
//         controls.update();
//     }

//     if (mixer) {
//         mixer.update(0.01); // Update the mixer on each frame
//     }

//     render();
// }

// function render() {
//     renderer.render(scene, camera);
// }

// animate();

// window.addEventListener('resize', function () {
//     var width = container.clientWidth;
//     var height = container.clientHeight;
//     renderer.setSize(width, height);
//     camera.aspect = width / height;
//     camera.updateProjectionMatrix();
// });
