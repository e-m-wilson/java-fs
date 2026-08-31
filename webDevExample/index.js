let btn = document.querySelector('#btn');
let p = document.querySelector('#joke');

btn.addEventListener('click', handleIt);

async function handleIt() {
    // https://api.chucknorris.io/jokes/random?category={category}
    
    let response = await fetch('https://api.chucknorris.io/jokes/random?category=dev');
    let parsedResponse = await response.json()

    p.innerHTML = parsedResponse.value;
}