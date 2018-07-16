package jdroidcoder.ua.chill.event

import jdroidcoder.ua.chill.response.CollectionItem

/**
 * Created by jdroidcoder on 13.07.2018.
 */
data class PlayAudio(var collectionItem: CollectionItem, var startFrom: Int = 0)