export default function reducer(state = {
  randomNumber: null
}, action) {

  switch (action.type) {
    case 'RANDOM_NUMBER_EVENT':
      return {
        ...state,
        randomNumber: action.payload
      };

    default:
      return state;
  }
}
