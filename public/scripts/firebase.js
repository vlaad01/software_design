const firebaseConfig = {
    apiKey: "AIzaSyBq2T1eKRhPTfGEDQivZPBYdy238lsHreE",
    authDomain: "songsandlyrics-716e5.firebaseapp.com",
    databaseURL: "https://songsandlyrics-716e5-default-rtdb.firebaseio.com",
    projectId: "songsandlyrics-716e5",
    storageBucket: "songsandlyrics-716e5.appspot.com",
    messagingSenderId: "598047773772",
    appId: "1:598047773772:web:3161d479779fb0e0eb50e9",
    measurementId: "G-5NGT7YGK1X"
  };

firebase.initializeApp(firebaseConfig);
firebase.analytics();

let currentUser = null
firebase.auth().onAuthStateChanged(user => {
    currentUser = user
})

class SongDao {
    root = firebase.firestore()

    async fetchSongs() {
        const ref = this.root.collection('songs')
        const snapshot = await ref.get()
        const songs = snapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data()
        }))
        return songs
    }

    async fetchUserSongs(user_id) {
        const ref = this.root.collection('songs')
            .where('user_id', '==', user_id)
        const snapshot = await ref.get()
        const songs = snapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data()
        }))
        return songs
    }

    async getSongById(song_id) { 
        let song = 0
        const snap = await this.root.collection('songs')
            .doc(song_id).get().then(doc => song = doc.data())
        return song
    }

    async deleteSong(song_id) {
        const snap = await this.root.collection('songs')
            .doc(song_id).delete()
        return true
    }

    async updateSong(song) {
        const snap = await this.root.collection('songs')
            .doc(song.id).set(song, {merge: true})
        return true
    }

    async uploadImage(file) {
        const name = new Date() + '-' + file.name
        const ref = firebase.storage().ref().child(name)
        const metadata = {
            contentType: file.type,
        }
        await ref.put(file, metadata)
        const url = await ref.getDownloadURL()
        return url
    }

    async createSong(song, image_file) {
        const img_ref = await this.uploadImage(image_file)
        song['img_url'] = img_ref
        const snippet = await this.root.collection("songs").add(song)
        return true
    }
}

class AnnotationDao {
    root = firebase.firestore()

    async fetchAnnotations(song_id) {
        const ref = this.root.collection('songs')
            .doc(song_id).collection('annotations')
    
        const snapshot = await ref.get()
        const annotations = snapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }))
        return annotations
    }

    async createAnnotation(annotation, song_id) {
        const ref = this.root.collection(`songs`).doc(song_id)
            .collection('annotations')
        const snippet = await ref.add(annotation)
        return snippet['id']
    }
}

class CommentDao {
    root = firebase.firestore()

    async fetchComments(song_id) {
        const ref = this.root.collection('songs')
            .doc(song_id).collection('comments')

        const snapshot = await ref.get()
        const comments = snapshot.docs.map((doc) => ({
            id: doc.id, 
            ...doc.data(),
        }))
        return comments
    }

    createComment(comment, song_id) {
        const ref = this.root.collection(`songs`).doc(song_id)
            .collection('comments')
        const snapshot = ref.add(comment)
    }

}

class LyricDao {
    root = firebase.firestore()

    async updateLyric(text, song_id, lyric_id) {
        const ref = this.root.collection('songs').doc(song_id)
            .collection('lyrics').doc(lyric_id)
        const snapshot = await ref.update({
            text: text,
        })
        return true
    }
    
    async createLyric(lyric, song_id) {
        const ref = this.root.collection('songs').doc(song_id)
            .collection('lyrics')
        const snapshot = await ref.add(lyric)
        return true
    }

    async deleteLyric(song_id, lyric_id) {
        const ref = this.root.collection('songs').doc(song_id)
            .collection('lyrics').doc(lyric_id)
        const snap = await ref.delete()
        return true
    }

    async fetchLyrics(song_id) {
        const ref = this.root.collection('songs').doc(song_id)
            .collection('lyrics')
        const snapshot = await ref.get()
        const lyrics = snapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data()
        }))
        return lyrics
    }

    async fetchUserLyrics(user_id) {
        const lyricRef = this.root.collectionGroup("lyrics")
            .where('author', '==', user_id)
        let snapshot = await lyricRef.get()
        const lyrics = snapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data()
        }))
        const songDao = new SongDao()
        let songs = []
        for (let lyric of lyrics) {
            const temp = await songDao.getSongById(lyric['song_id'])
            songs.push(temp)
        }
        return lyrics
    }
}

class LikesDao {
    root = firebase.firestore()

    getPathForSong(song_id) {
        return this.root.collection('songs').doc(song_id)
    }

    getPathForComment(song_id, comment_id) {
        return this.getPathForSong(song_id).collection('comments')
            .doc(comment_id)
    }

    async getLikes(path) {
        const snap = await path.collection('likes').get()
        const likes = snap.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }))

        return likes
    }

    async getDislikes(path) {
        const snap = await path.collection('dislikes').get()
        const dislikes = snap.docs.map((doc) => ({
            id: doc.id,
            ...doc.data()
        }))

        return dislikes
    }

    async isUserLiked(user_id, path) {
        const likes = await this.getLikes(path)
        return likes.some(val => val.id == user_id)
    }

    async isUserDisliked(user_id, path) {
        const dislikes = await this.getDislikes(path)
        return dislikes.some(val => val.id == user_id)
    }

    addLike(user_id, ref) {
        ref.collection('likes').doc(user_id).set({}, {merge: true})
    }

    addDislike(user_id, ref) {
        ref.collection('dislikes').doc(user_id).set({}, {merge: true})
    }
}