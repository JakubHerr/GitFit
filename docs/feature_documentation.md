## Feature documentation
The application has the following major features and use cases:

# Offline-first data synchronization
- Application uses Google Firebase backend and GitLive Firebase SDK for user authentication and data synchronization
- The majority of features will work with no internet connection on mobile devices
- On desktop, GitLive Firebase SDK does not currently support caching and internet connection is required at least for login
- User MUST be connected to the internet when deleting their account to clear online database
- without internet connection, the user can do the following actions:
- start and finish an unplanned workout without internet
- create custom exercise
- create custom plan
- start and finish a planned workout with progression
- log out of their account
- browse cached workout and exercise history

# Authentication

##### User registration
- User must register using email and password to use the application
- User can validate their email address, but it is not required. A verification link will be sent to their email

##### User sign in
- User can sign into an account using their email and password
- User can request a password reset if they forgot their password. A password reset link will be sent to their email

##### User deletion
- User can delete their account from their profile
- on account deletion, all user data is deleted
- User can request account deletion from outside of the application (https://jakubherr.github.io/)


# Workout planning

#### Plan creation
- User can create a custom plan
- A plan is made of one or many workout plans/days
- Workout day consists of blocks of exercises with sets
- user can rename workout day
- User can start a new workout record from a workout day in a plan
- User can delete workout days in a plan or the entire plan

#### Basic progressive overload
- User can define settings to make their custom plan harder over time
- Overload is tracked for every exercise in plan separately
- Progressive overload settings are based on a progressed value, threshold and increment
- currently, either weight or repetitions can be progressed
- a threshold is the minimum weight and number of repetitions in each set of an exercise
- an increment is the amount that either weight or repetitions will be incremented when threshold is reached
- for progressive overload to work, user must start workout from a predefined plan
- after the workout is completed, the plan is automatically updated if a threshold was reached

### Customization
- user can create custom exercise
- custom exercise has a name, primary and secondary worked muscle

### Workout recording
- Start a new unplanned workout or select one from a created plan
- Add predefined or custom exercise to workout
- remove exercise from workout
- Add work sets to each exercise
- Input weight, repetitions and toggle completion on each set
- User can remove last set from exercise
- Discard in-progress workout
- Save finished workout to database

### User body measurements
- user can input their body measurements
- measurements are tracked over time on a graph
- user can browse all previous measurements
- user can delete measurements

### Data visualization with graphs
- user can observe exercise and body measurement progress over time
- exercise data is located in exercise detail
- measurement data is located in Measure in main navigation

# User input validation
- all user input must be validated
- String user input is generally limited to 20 characters with exception for email and password fields

#### Workout plan
- workout plan must have a valid name
- workout plan must contain at least one workout day
- every workout day must contain at least one block with exercise
- every exercise block must contain at least one set that is not empty
- every set must contain valid input for weight and repetitions
- valid weight is a non-negative number with up to two decimal points
- valid repetition is a whole non-negative number (user wants to record failed lift)

#### Measurement
- all measurements are currently in cm and kg
- valid measurement is a non-negative number
- measurement input is limited to two decimal points
- currently, all measurement values must be input to be a valid record

### Navigation
- If user is not signed-in, the app starts with a login screen
- (mobile only) If a user was signed-in already, the app starts with a dashboard screen
- User can navigate between top-level destinations using a navigation bar
- When user is exiting an operation with unsaved progress (workout plan), they must confirm this action
