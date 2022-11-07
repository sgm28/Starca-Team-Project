# Starca

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview

### Description
Starca is a storage marketplace that allows users to either list their unusued space to make some extra income or look for listed places so they can store items that are taking up lots of space in their household.

### App Evaluation

- **Category:** P2P Marketplace
- **Mobile:** This app is being primarily developed for mobile but would perhaps be just as usable on a computer, similarly to Zillow, Apartments, etc. Mobile version would provide for better features and prove to be more convenient, as you would want to search/manage rental properties while not constricted to a computer.
- **Story:** Allow landlords to post unused storage spaces for tenants to search for. Connects people looking to rent their storage space and people who are looking for a storage space at a rate best for them.
- **Market:** Individuals that are looking to make some extra income by renting out unused space and those that need to clear up space in their house but don't want to pay the high prices for traditional storage facilities
- **Habit:** This app is not meant to be used consistently. Renters and rentees can "set it and forget it"
- **Scope:** Start by allowing users to post their storage spaces for rent and allowing users to search for storage spaces. Provide a platform for these two user groups to connect.  

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users will be able to register an account
* Users will be able to log in to their account
* Users will be able to view listings
* Users will be able to view listings of storage places to rent nearby
* Users will be able to view listings in other selected areas
* Users will be able to post listings
  * Users will be able to post pictures (implicit intent) for listings
  * User will be able post details on their storage listing
* Users will be able to update their profile
* Users will have a dashboard to select listings/rentals they own
* Users will be able to rate renters
* Renter will be able to rate rentees
* Users will be able to view previously rented storage listing
* Users will be able to delete their account
* Users will be able to chat with their prospective renters/rentees

**Optional Nice-to-have Stories**

* [fill in your optional user stories here]
* ...

### 2. Screen Archetypes

* Login
   * Users can log into their account
* Register
   * Users can register for an account
* Dashboard (Maps)
   * Users will see a maps screen that displays nearby listings to their current location, and a list of the listings below it with some more details for each property.
* Listing Creation
   * Users will be able to post listings to the app
   * Fragment 1
      * Users upload pictures of their space
   * Fragment 2
      * Users enter details of the property (address
   * Fragment 3
      * Users enter more details (amenaties, etc) into the listing
* Listing Detail
   * Show property details for a listing (Zip Code, photos, amenaties)
   * Show reviews for the property left by other users.
* Messaging (Stream)
   * Display all the users that have been messaged 
* Messaging Detail Page
   * Display the messages two users have sent to each other. Allow a user to send a message to another user.
* Profile
   * Users can add or change their profile photo
   * Users can see reviews about them left by other users.
* Settings
   * Users can delete their account
   * Users can log out of their account
* OPTIONAL STRETCH Stream Page (Search)
   * Users can search a zipcode to show all of the available storeage spaces in that zip code


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Dashboard
* Post
* Message
* Profile

**Flow Navigation** (Screen to Screen)

* Home
   * Login - Bottom Nav
     * Dashboard
   * Register - Bottom Nav
     * Dashboard
* Dashboard + Search
  * Search
    * Details
  * Details
  * Post - Bottom Nav
    * Post
  * Message - Bottom Nav
    * List of message
      * Message
    * Message
  * Profile - Bottom Nav
    * Settings

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
