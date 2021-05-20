const songDao = new SongDao()

async function uploadSongs() {
    songs = await songDao.fetchSongs()
    grid = document.getElementsByClassName('grid')[0]
    let counter = 1
    for (song of songs) {
        
        link = document.createElement("a")
        link.className = "grid__row"
        link.href = "lyric.html?id=" + song.id

        div = document.createElement('div')
        div.className = "grid__item"
        div.innerHTML = counter.toString()
        link.appendChild(div)

        img = document.createElement("img")
        img.className = "author-image"
        img.src = song['img_url']
        link.appendChild(img)
        

        title = document.createElement('h3')
        title.className = "grid__item"
        title.innerHTML = song['title']
        link.appendChild(title)

        author = document.createElement('h4')
        author.className = "grid__item"
        author.innerHTML = song['author']
        link.appendChild(author)

        grid.appendChild(link)
        counter++
    }
}

uploadSongs()