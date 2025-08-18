const person = {
    name: 'Aleks',
    adress: {
        line: '650',
        city: 'Cacak',
        country: "Serbia"
    },
    profiles: ['twitter', 'Facebook'],
    // printProfile: function() {
    //     this.profiles.map((profile) => {
    //         console.log(profile);
    //     });
    // }
}


export default function LearningJavaScript() {
    return (
        <>
            <div>{ person.name}</div>
            <div>{person.adress.line} { person.adress.country}</div>
        </>
    )
}