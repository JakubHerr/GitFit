## Feature documentation
The application has the following major features and use cases:

# Offline-first data synchronization
- Application uses Google Firebase for authentication and data synchronization
- The majority of features will work with no internet connection on mobile devices
- On desktop, Google Firebase SDK does not currently support offline-first and internet connection is required
- without internet connection, the user can do the following actions:
- **TODO workout completion gets stuck with no internet**

# Authentication

##### User registration
- User must register using email and password to use the application
- User can validate their email address, but it is not required. A verification link will be sent to their email

##### User sign in
- User can sign into an account using their email and password
- User can request a password reset if they forgot their password. A password reset link will be sent to their email

##### User deletion
- User can delete their account from their profile
- TODO on account deletion, all user data is deleted
- TODO for obvious reasons, user must be connected to the internet to delete data from database
- TODO User can request account deletion from outside of the application


# Workout planning

#### Plan creation
- User can create a custom plan
- A plan is made of one or many workout plans/days
- Workout day consists of blocks of exercises with sets
- TODO user can rename workout day
- User can save a created plan to database
- User can start a new workout record from a workout day in a plan
- User can delete workout days in a plan or the entire plan

#### Basic progressive overload
- User can define settings to make their custom plan harder over time
- Overload is tracked for every exercise in plan separately
- Progressive overload settings are based on a progresed value, threshold and increment
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
- TODO remove exercise from workout
- Add work sets to each exercise
- Input weight, repetitions and toggle completion on each set
- TODO User can remove set from exercise
- Discard in-progress workout
- Save finished workout to database

### User body measurements
- user can input their body measurements
- measurements are tracked over time on a graph
- TODO user can browse all previous records
- TODO user can delete records

### Data visualization with graphs
- user can observe exercise and body measurement progress over time
- exercise data is located in exercise detail
- measurement data is located in Measure in main navigation

# User input validation
- all user input must be validated
- TODO limit string input lenght for plan name, exercise name etc.

#### Auth
- User's password must be at least 12 characters long

#### Workout plan
- workout plan must have a valid name
- workout plan must contain at least one workout day
- every workout day must contain at least one block with exercise
- every exercise block must contain at least one set that is not empty
- every set must contain valid input for weight and repetitions
- valid weight is a non-negative number with up to two decimal points
- User can define progressive overoad settings for each exercise block in workout day
- overload is optional
- TODO valid repetition is a positive whole number

#### Measurement
- all measurements are currently in cm and kg
- valid measurement is a non-negative number
- TODO measurement input is limited to two decimal points
- currently, all measurement values must be input to be a valid record

### Navigation
- If user is not signed-in, the app starts with a login screen
- (mobile only) If a user was signed-in already, the app starts with a dashboard scren
- User can navigate between top-level destinations using a navigation bar
- TODO user can navigate to the previous destination using a back button
- User can start a predefined workout by clicking on a plan and selecting a workout day

