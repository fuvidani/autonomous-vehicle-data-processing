export default function reducer(state = {
  randomNumber: null,
  notification: null
}, action) {

  switch (action.type) {
    case 'RANDOM_NUMBER_EVENT':
      return {
        ...state,
        randomNumber: action.payload
      };

    case 'NOTIFICATION_EVENT':
      return {
        ...state,
        notification: action.payload
      };

    default:
      return state;
  }
}
