let currUser = null
firebase.auth().onAuthStateChanged(user => {
    currUser = user
    if (user !== null) {
        updateUserSongs(user)
    }
})

const lyricDao = new LyricDao()
const songDao = new SongDao()

let index = 0
const songSelect = document.getElementById("song-select")
let songs = []
let songId = null
let lyricId = null

function createLyric() {
    event.preventDefault()
    lyric = getTextData()
    lyric['song_id'] = songs[index - 1].id
    lyric['author'] = currUser.uid
    const lyricDao = new LyricDao()
    if (currUser !== null) {
        lyricDao.createLyric(lyric, songs[index - 1].id).then(() => {
            location.href = "profile.html"
        })
    }
    else {
        window.href = 'index.html'
    }
}

async function updateLyric() {
    event.preventDefault()
    data = getTextData()
    await lyricDao.updateLyric(convertNewLinesToBr(data['text']), songId, lyricId)
        .then(window.location.replace('profile.html'))
}

async function deleteLyric() {
    event.preventDefault()
    await lyricDao.deleteLyric(songId, lyricId)
        .then(window.location.replace('profile.html'))
}

function getTextData() {
    return {
        language: document.getElementById('language').value,
        text: convertNewLinesToBr(document.getElementById('lyric').value)
    }
}

function updateUserSongs(user) {
    songDao.fetchUserSongs(user.uid).then(data => {
        songs = data
        songs.forEach(song => {
            let opt = document.createElement('option')
            opt.value = song['title']
            opt.innerHTML = song['title']
            songSelect.appendChild(opt)
        });
    })
}

function getSelectedSong() {
    index = select.selectedIndex
}

async function OnLoad() {
    params = new URLSearchParams(window.location.search)
    songId = params.get('id')
    lyricId = params.get('lyric')

    if (songId !== null) {
        const song = await songDao.getSongById(songId)
        const lyrics = await lyricDao.fetchLyrics(songId)
        const lyric = lyrics.find(item => item.id == lyricId)

        document.getElementById('lyric').value = convertBrToNewLines(lyric.text)
        document.getElementById('language').value = lyric.language
        let select = `<option selected>${song.title}</option>`
        songSelect.innerHTML += select
        songSelect.disabled = true
        document.getElementById('btn-add').hidden = true
    } else {
        document.getElementById('btn-update').hidden = true
        document.getElementById('btn-delete').hidden = true
    }
}

function convertNewLinesToBr(str) {
    return str.replace(/(?:\r\n|\r|\n)/g, '<br>');
}

function convertBrToNewLines(str) {
    return str.replace(/\s?(<br\s?\/?>)\s?/g, "\r\n");
}




