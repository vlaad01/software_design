const btnLogin = document.getElementById('btn-login')
const profileLink = document.getElementById('profile-link')
const btnLogout = document.getElementById('btn-logout')

btnLogin.addEventListener('click', () => {
    const provider = new firebase.auth.GoogleAuthProvider()

    firebase.auth().signInWithPopup(provider)
        .then( result => {
            console.log(result.user)
        })
        .catch( error => {
            console.error(error)
        })
})

btnLogout.addEventListener('click', () => {
    firebase.auth().signOut()
        .then(window.location.replace('index.html'))
})

firebase.auth().onAuthStateChanged(user => {
    if (user) {
        btnLogin.hidden = true
        profileLink.hidden = false
        btnLogout.hidden = false
    } else {
        btnLogin.hidden = false
        profileLink.hidden = true
        btnLogout.hidden = true
    }
})