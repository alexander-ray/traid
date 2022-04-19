# Overview
Currently this is a generic set of tools and utilities for interacting with financial APIs, partially out of curiosity
and partially for practical use. All written in Kotlin, because I like it.

At the time of writing, the main piece of functionality is the `TraidStore` which is a simple interface for querying 
timeseries stock data, along with necessary cacheing. Data currently is sourced from AlphaVantage.

# Editor's Notes
This is mostly for fun, so I'm purposefully not always adopting libraries/tools that would make life easier, solely
because writing code is enjoyable and it's not necessary. Some examples include community versions of API sdks, etc; in 
an ideal world, if we get to the point of wanting things Done Right(tm), code is structured such that we can make some drop-in
replacements. 

# TODOs
* log4j properties aren't working right
* depending on how beefy this gets, let's bring in dagger
* adding unit tests (classic)
* start working on a data catalog for the TraidStore. Such a broad concept we should probably hold off.
* adding support for intraday data. We could also add support for more aggregated data, but the data volumes for daily data
are small enough we may never need to
* integrate with alpaca for paper trading
* expand scenarios package
* add in some simple analysis tools; more complex stuff probably wants to be pandas, but basic graphs and stuff are good
* bring in broader variety of data (fundamentals, indicators, various dimensions, crypto(?), etc)
  * alpaca has a news api, which is pretty cool