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
        .addEventListener('click', _ => likesDao.addLike(currUser.uid, songRef))

    document.getElementById('song-dislike-btn')
        .addEventListener('click', _ => likesDao.addDislike(currUser.uid, songRef))
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
        const comment = `
            <div class="comment" id=${item.id}>
                <span class"comment__author">${item['user_id']}</span>
                <p class="comment__text">${item['text']}</p>
                <button class="like-button" onclick="commentBtnLikeClicked(this)"></button>
                <button class="dislike-button" onclick="commentBtnDislikeClicked(this)"></button>
            </div>`
        commentsSection.innerHTML += comment
    }
}

async function addComment() {
    event.preventDefault()
    textArea = document.getElementById("comment-text")
    text = textArea.value
    comment = {
        user_id: currUser.uid,
        text: text,
    }
    await commentDao.createComment(comment, song_id)
    commentsSection.innerHTML = ""
    textArea.value = ""
    loadComments()
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

function commentBtnLikeClicked(target) {
    const commentId = target.parentNode.id
    const ref = likesDao.getPathForComment(song_id, commentId)
    likesDao.addLike(currUser.uid, ref)
}

function commentBtnDislikeClicked(target) {
    const commentId = target.parentNode.id
    const ref = likesDao.getPathForComment(song_id, commentId)
    likesDao.addDislike(currUser.uid, ref)
}
