package com.tpe.payload.messages;

public class ErrorMessages {



    private ErrorMessages() {
  }

    public static final String USER_NOT_ADMIN = "User is not Admin";

  public static final String NOT_PERMITTED_METHOD_MESSAGE="You don't have any authority for this...";






    public static final String ROLE_NOT_FOUND = "Role doesn't exist";

    public static final String ALREADY_REGISTER_MESSAGE_EMAIL= "Email is exists already : %s";
    public static final String ALREADY_REGISTER_MESSAGE_PHONE= "Email is exists already : %s";

    public static final String USER_HAS_EXPIRE_LOAN = "User has expired date loan by id : %s";
    public static final String USER_HAS_LOAN = "User has loan, so user cant be deleted";
    public static final String USER_CAN_NOT_LOAN = "User can not loan this book now. Check loanlist and score";
    public static final String NOT_FOUND_USER_MESSAGE = "Error: User not found with id %s";

    public static final String LOAN_NOT_FOUND="Loan is not found by id : %s";
    public static final String LOAN_NOT_FOUND_BY_USER="This loan does not belong to this user id: %s";

    public static final String DONT_HAVE_AUTHORITY="You don't have permission to do that";


  public static final String CUSTOMER_CAN_ONLY_UPDATE_OWN_MESSAGE = "Customer can only update own information.";






}
