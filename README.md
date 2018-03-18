# SkipTheDishes mobile challenge

This project was implemented as part of the VanHack & SkipTheDishes hackathon.
The objective is to construct a solution which interacts with skip's API to show products, allow customers to add products to a cart, place and review orders.

## Technologies

Some of the technologies used include Java for Android, retrofit, RxJava and RxAndroid

## MVP Based Architecture

In order to minimize code coupling and allow a clear and more flexible implementation, the screens classes were constructed using an architecture based on MVP (model, view, presenter), isolating the business rules, persistent data and API acesses from the views.