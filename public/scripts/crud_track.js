let currUser = null
let songId = null
const db = new SongDao()

firebase.auth().onAuthStateChanged(user => {
    currUser = user
    if (currUser === null) {
        window.href = 'index.html'
    }
})

function getData() {
    return {
        title: document.getElementById('title').value,
        album: document.getElementById('album').value,
        author: document.getElementById('author').value,
        user_id: currUser.uid,
    }
}

function UploadTrack() {
    event.preventDefault()
    const file = document.getElementById('image').files[0]

    if (currUser !== null)
        db.createSong(getData(), file)
            .then(_ => {
                location.href = "profile.html"
            })
}

async function deleteTrack() {
    event.preventDefault()
    await db.deleteSong(songId)
    window.location.replace('profile.html')
}

async function updateTrack() {
    event.preventDefault()
    data = getData()
    data['id'] = songId
    await db.updateSong(data)
    window.location.replace('profile.html')
}

function OnLoad() {
    params = new URLSearchParams(window.location.search)
    songId = params.get('id')
    if (songId !== null) {
        document.getElementById('btn-add').hidden = true
        document.getElementById('image').hidden = true
        document.getElementById('image-label').hidden = true
        db.getSongById(songId).then(song => {
            document.getElementById('title').value = song.title
            document.getElementById('author').value = song.author
            document.getElementById('album').value = song.album
        })
    } else {
        document.getElementById('btn-update').hidden = true
        document.getElementById('btn-delete').hidden = true
    }
}