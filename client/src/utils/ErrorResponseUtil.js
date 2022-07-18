const ErrorResponseUtil = {
  loggedInNewState(errorResponse) {
    return errorResponse.response.status !== 401;
  },
};

export default ErrorResponseUtil;
