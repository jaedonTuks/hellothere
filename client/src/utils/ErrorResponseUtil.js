const ErrorResponseUtil = {
  loggedInNewState(errorResponse) {
    if (!errorResponse.response || !errorResponse.response.status) {
      return true;
    }
    return errorResponse.response.status !== 401;
  },
};

export default ErrorResponseUtil;
