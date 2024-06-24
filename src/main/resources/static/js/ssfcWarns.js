function main() {
    document.querySelector('.ssfc-warns .warns-ok')
        .addEventListener('click', (e) => {
            document.querySelector('.ssfc-warns').style.display = 'none'
            console.log('Ssfc warns ok button clicked and hidden');
        });
}

main();