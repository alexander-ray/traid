package store

/**
 * Store for accessing Traid data, abstracting away most of the retrieval and cacheing.
 *
 * At the time of writing, the only data available is stock timeseries data, but eventually we might
 * add more types of data (which may or may not require different stores).
 *
 * The store uses adapters to external data sources as a mechanism for bringing data locally.
 */
class TraidStore {

}