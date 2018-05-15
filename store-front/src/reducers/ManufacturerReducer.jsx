export default function reducer(state = {
    manufacturers: [
        {
            id: "audi_id",
            name: "audi"
        },
        {
            id: "mercedes_id",
            name: "mercedes"
        },
        {
            id: "porsche_id",
            name: "porsche"
        },
        {
            id: "opel_id",
            name: "opel"
        }
    ]
}, action) {

    switch (action.type) {
        default:
            return state;
    }
}
