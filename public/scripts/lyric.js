const lyricDao = new LyricDao()
const annotationDao = new AnnotationDao()
const songDao = new SongDao()
const commentDao = new CommentDao()
const likesDao = new LikesDao() 

let aside = document.getElementsByClassName("annotation")[0]
let commentsSection = document.getElementsByClassName("comments")[0]

let song_id = ""
let selectedText = ""
let lyrics_id = ""

let currUser = null

firebase.auth().onAuthStateChanged(user => {
    currUser = user
})


function OnLoad() {
    params = new URLSearchParams(window.location.search)
    song_id = params.get("id")
    lyricId = params.get("lyrics")
    loadSong(song_id)
    loadLyricsList(song_id, lyricId)
    loadComments(song_id)

    const article = document.getElementsByClassName('lyric')[0]
    article.addEventListener("mouseup", getSelectedText)
    article.addEventListener("mousedown", mouseDownEvent)
}

async function loadSong(song_id) {
    const song = await songDao.getSongById(song_id)
    const songRef = likesDao.getPathForSong(song_id)

    let img = document.getElementsByClassName('author__img')[0]
    img.src = song['img_url']

    document.getElementById('title')
        .innerHTML = song['title']

    document.getElementById('author-name')
        .innerHTML = song['author']

    document.getElementById('album')
        .innerHTML = song['album']

    document.getElementById('song-like-btn')
        .addEventListener('click', songBtnLikeClicked)

    document.getElementById('song-dislike-btn')
        .addEventListener('click', songBtnDislikeClicked)
}

async function loadLyricsList(songId, lyricsId) {
    const lyricsList = await lyricDao.fetchLyrics(songId)

    if (lyricsId === null) 
        lyricsId = lyricsList[0].id
    lyrics = lyricsList.find(item => item.id == lyricsId)

    updateText(lyrics, songId)

    list = document.getElementById('translations__list')
    for (lyrics of lyricsList) {
        li = document.createElement('li')
        li.id = lyrics.id
        li.addEventListener('click', event => {
                newId = event.target.id
                newLyrics = lyricsList.find(l => l.id == newId)
                updateText(newLyrics, songId)
            })
        li.innerHTML = lyrics.language
        list.appendChild(li)
    }
}

async function updateText(lyrics, songId) {
    lyrics_id = lyrics.id
    const article = document.getElementsByClassName('lyric')[0]
    article.innerHTML = lyrics['text']

    const annotations = await annotationDao.fetchAnnotations(songId)
    const marks = document.getElementsByTagName('mark')

    for (mark of marks) {
        mark.addEventListener("click", event => {
            aside.classList.toggle("hidden")
            let text = document.getElementById('annotation__text')
            text.innerHTML = annotations.find(item => item.id == event.target.id).text
        })
    }
}

async function addAnnotation() {
    event.preventDefault()
    textArea = document.getElementById('annotation-text')
    aside.classList.add("hidden")

    let lyrics = document.getElementsByClassName('lyric')[0].innerHTML

    const regexp = new RegExp(`${selectedText}*`, 'g')
    const matchAll = lyrics.matchAll(regexp)
    const matches = Array.from(matchAll)

    let offset = 0
    const annotationId = await annotationDao.createAnnotation({text: textArea.value}, song_id)

    for (match of matches) {
        const mark_start = `<mark class="lyric__marked" id="${annotationId}">`
        const mark_end = '</mark>'
        lyrics = insert(lyrics, mark_start, match['index'] + offset)
        offset += mark_start.length
        lyrics = insert(lyrics, mark_end, match['index'] + selectedText.length + 1 + offset)
        offset += mark_end.length;

    }
    offset = 0
    await lyricDao.updateLyric(lyrics, song_id, lyrics_id)
}

async function loadComments(song_id) {
    comments = await commentDao.fetchComments(song_id)

    for (item of comments) {
        const ref = likesDao.getPathForComment(song_id, item.id)
        isUserLiked = await likesDao.isUserLiked(currUser.uid, ref)
        isUserDisliked = await likesDao.isUserDisliked(currUser.uid, ref)
        const comment = `
            <div class="comment" id=${item.id}>
                <span class"comment__author">${item['user_id']}</span>
                <p class="comment__text">${item['text']}</p>
                <button class="like-button" onclick="commentBtnLikeClicked(this)">${getLikeIcon(isUserLiked)}</button>
                <button class="dislike-button" onclick="commentBtnDislikeClicked(this)">${getDislikeIcon(isUserDisliked)}</button>
            </div>`
        commentsSection.innerHTML += comment
    }
}

async function addComment() {
    event.preventDefault()
    textArea = document.getElementById("comment-text")
    text = textArea.value
    comment = {
        user_id: currUser.displayName,
        text: text,
    }
    await commentDao.createComment(comment, song_id)
    commentsSection.innerHTML = ""
    textArea.value = ""
    loadComments(song_id)
}

function getSelectedText(event) {
    selectedText = window.getSelection().toString().trim();
    if (selectedText.length) {
        if (aside.classList.contains('hidden')) {
            aside.classList.remove('hidden')
        }
    }
}

function insert(a, b, position) {
    return [a.slice(0, position), b, a.slice(position)].join('');
}

function mouseDownEvent(event) {
    if (event.target.id !== "annotation") {
        aside.classList.add('hidden')
        document.getElementById('annotation__text').innerText = ""
        window.getSelection().empty()
    }
}

async function songBtnLikeClicked(event) {
    const path = event.target.getElementsByTagName('path')[0]
    const ref = likesDao.getPathForSong(song_id)
    const resp = await likesDao.addLike(currUser.uid, ref)
    if (resp)
        path.setAttribute('fill', 'green')
}

async function songBtnDislikeClicked(event) {
    const path = event.target.getElementsByTagName('path')
    const ref = likesDao.getPathForSong(song_id)
    const resp = await likesDao.addDislike(currUser.uid, ref)
    if (resp)
        path[0].setAttribute('fill', 'red')
}

async function commentBtnLikeClicked(target) {
    const path = target.getElementsByTagName('path')
    const commentId = target.parentNode.id
    const ref = likesDao.getPathForComment(song_id, commentId)
    const resp = await likesDao.addLike(currUser.uid, ref)
    if (resp)
        path[0].setAttribute('fill', 'green')
}

async function commentBtnDislikeClicked(target) {
    const path = target.getElementsByTagName('path')
    
    const commentId = target.parentNode.id
    const ref = likesDao.getPathForComment(song_id, commentId)
    const resp = await likesDao.addDislike(currUser.uid, ref)
    if (resp)
        path[0].setAttribute('fill', 'red')
}

function getLikeIcon(userLiked) {
    let currentColor = ''
    if (userLiked) 
        currentColor = 'green'
    return `<svg aria-hidden="true" focusable="false" data-prefix="far" data-icon="thumbs-up" class="svg-inline--fa fa-thumbs-up fa-w-16" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path fill="${currentColor}" d="M466.27 286.69C475.04 271.84 480 256 480 236.85c0-44.015-37.218-85.58-85.82-85.58H357.7c4.92-12.81 8.85-28.13 8.85-46.54C366.55 31.936 328.86 0 271.28 0c-61.607 0-58.093 94.933-71.76 108.6-22.747 22.747-49.615 66.447-68.76 83.4H32c-17.673 0-32 14.327-32 32v240c0 17.673 14.327 32 32 32h64c14.893 0 27.408-10.174 30.978-23.95 44.509 1.001 75.06 39.94 177.802 39.94 7.22 0 15.22.01 22.22.01 77.117 0 111.986-39.423 112.94-95.33 13.319-18.425 20.299-43.122 17.34-66.99 9.854-18.452 13.664-40.343 8.99-62.99zm-61.75 53.83c12.56 21.13 1.26 49.41-13.94 57.57 7.7 48.78-17.608 65.9-53.12 65.9h-37.82c-71.639 0-118.029-37.82-171.64-37.82V240h10.92c28.36 0 67.98-70.89 94.54-97.46 28.36-28.36 18.91-75.63 37.82-94.54 47.27 0 47.27 32.98 47.27 56.73 0 39.17-28.36 56.72-28.36 94.54h103.99c21.11 0 37.73 18.91 37.82 37.82.09 18.9-12.82 37.81-22.27 37.81 13.489 14.555 16.371 45.236-5.21 65.62zM88 432c0 13.255-10.745 24-24 24s-24-10.745-24-24 10.745-24 24-24 24 10.745 24 24z"></path></svg>`

}

function getDislikeIcon(userDisliked) {
    let currentColor = ''
    if (userDisliked) 
        currentColor = 'red'
    return `<svg aria-hidden="true" focusable="false" data-prefix="far" data-icon="thumbs-down" class="svg-inline--fa fa-thumbs-down fa-w-16" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path fill="${currentColor}" d="M466.27 225.31c4.674-22.647.864-44.538-8.99-62.99 2.958-23.868-4.021-48.565-17.34-66.99C438.986 39.423 404.117 0 327 0c-7 0-15 .01-22.22.01C201.195.01 168.997 40 128 40h-10.845c-5.64-4.975-13.042-8-21.155-8H32C14.327 32 0 46.327 0 64v240c0 17.673 14.327 32 32 32h64c11.842 0 22.175-6.438 27.708-16h7.052c19.146 16.953 46.013 60.653 68.76 83.4 13.667 13.667 10.153 108.6 71.76 108.6 57.58 0 95.27-31.936 95.27-104.73 0-18.41-3.93-33.73-8.85-46.54h36.48c48.602 0 85.82-41.565 85.82-85.58 0-19.15-4.96-34.99-13.73-49.84zM64 296c-13.255 0-24-10.745-24-24s10.745-24 24-24 24 10.745 24 24-10.745 24-24 24zm330.18 16.73H290.19c0 37.82 28.36 55.37 28.36 94.54 0 23.75 0 56.73-47.27 56.73-18.91-18.91-9.46-66.18-37.82-94.54C206.9 342.89 167.28 272 138.92 272H128V85.83c53.611 0 100.001-37.82 171.64-37.82h37.82c35.512 0 60.82 17.12 53.12 65.9 15.2 8.16 26.5 36.44 13.94 57.57 21.581 20.384 18.699 51.065 5.21 65.62 9.45 0 22.36 18.91 22.27 37.81-.09 18.91-16.71 37.82-37.82 37.82z"></path></svg>`

}
