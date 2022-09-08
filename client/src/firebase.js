import { initializeApp } from 'firebase/app';
import { getMessaging } from 'firebase/messaging';

const firebaseConfig = {
  apiKey: 'AIzaSyB3k8jcPMNchBIxvLdzzARYvZR3iyd3z4k',
  authDomain: 'university-honors-project.firebaseapp.com',
  projectId: 'university-honors-project',
  storageBucket: 'university-honors-project.appspot.com',
  messagingSenderId: '325976335168',
  appId: '1:325976335168:web:5c4c1a045fd5c12221d0a5',
};

const app = initializeApp(firebaseConfig);

export default getMessaging(app);
