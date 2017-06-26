Feature: User

  #user-change-password
  Scenario: Change user password
    Given A user exists:
      | firstName | lastName | email | password | active |
      | John      | Change-Password | john-change-password@example.com | Abcd1234 | true |
    When The user's password is updated to 1234Abcd
    Then Users passwordChanged field is updated