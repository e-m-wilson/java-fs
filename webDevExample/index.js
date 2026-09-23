// let btn = document.querySelector('#btn');
// let p = document.querySelector('#joke');

// btn.addEventListener('click', handleIt);

// async function handleIt() {
//     // https://api.chucknorris.io/jokes/random?category={category}
    
//     let response = await fetch('https://api.chucknorris.io/jokes/random?category=dev');
//     let parsedResponse = await response.json()

//     p.innerHTML = parsedResponse.value;
// }


let form = document.querySelector('#form');

form.addEventListener('submit', searchForPokemon)

async function searchForPokemon(e) {
    e.preventDefault();
    let body = document.querySelector('body');
    let name = document.querySelector('#pokemonName').value;
    let response = await fetch(`https://pokeapi.co/api/v2/pokemon/${name}`);
    let parsedResponse = await response.json();
    console.log(parsedResponse);

    if(document.querySelector('#moveset')) {
        body.removeChild(document.querySelector('#moveset'))
    }

    if(document.querySelector('#sprite')) {
        body.removeChild(document.querySelector('#sprite'))
    }

    
    let sprite = document.createElement('img');
    sprite.src = parsedResponse.sprites.front_default;
    sprite.id = 'sprite';
    body.appendChild(sprite);

    let moveSet = document.createElement('div');
    moveSet.classList.add('moveset');
    moveSet.id = 'moveset'
    for(let i = 0; i<parsedResponse.moves.length; i++) {
        let span = document.createElement('span');
        span.textContent = parsedResponse.moves[i].move.name;
        moveSet.appendChild(span);
    }
    body.appendChild(moveSet);

}