firebase.auth().onAuthStateChanged(user => {
    if (user !== null) {
        updateUserSongs(user)
        updateUserLyrics(user)
        document.getElementById('user-name')
            .innerText = user.displayName
    }
})

async function updateUserSongs (currentUser) {
    const songDao = new SongDao()
    console.log(currentUser.uid)
    const userSongs = await songDao.fetchUserSongs(currentUser.uid)
    userSongs.forEach(song => {
        grid = document.getElementById('tracks-grid')
        newRow = `
        <a class="grid__row" href="crud_track.html?id=${song.id}">
            <h3 class="grid__item">${song.title}</h3>
            <h4 class="grid__item">${song.author}</h4>
            <h4 class="grid__item">${song.album}</h4>
        </a>
        `
        grid.innerHTML += newRow
    });
}

async function updateUserLyrics(currentUser) {
    const lyricDao = new LyricDao()
    const songDao = new SongDao()
    const userLyrics = await lyricDao.fetchUserLyrics(currentUser.uid)
    for (lyric of userLyrics) {
        song = await songDao.getSongById(lyric.song_id)

        lyricsList = document.getElementById('lyrics-grid')

        newRow = `
            <a class="grid__row" href="crud_lyric.html?id=${lyric.song_id}&lyric=${lyric.id}">
                <h3 class="grid__item">${song.title}</h3>
                <h4 class="grid__item">${song.author}</h4>
                <h4 class="grid__item">${lyric.language}</h4>
            </a>`

        lyricsList.innerHTML += newRow
    }
}



