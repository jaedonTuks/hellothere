/* eslint-disable no-undef */
importScripts('https://www.gstatic.com/firebasejs/8.2.7/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.2.7/firebase-messaging.js');

const firebaseConfig = {
  apiKey: 'AIzaSyB3k8jcPMNchBIxvLdzzARYvZR3iyd3z4k',
  authDomain: 'university-honors-project.firebaseapp.com',
  projectId: 'university-honors-project',
  storageBucket: 'university-honors-project.appspot.com',
  messagingSenderId: '325976335168',
  appId: '1:325976335168:web:5c4c1a045fd5c12221d0a5',
};

// eslint-disable-next-line no-unused-vars
const app = firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

messaging.onBackgroundMessage(() => {
  console.log('service worker background message');
});
