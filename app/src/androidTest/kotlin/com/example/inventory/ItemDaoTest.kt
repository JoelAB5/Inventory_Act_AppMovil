package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {
    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase
    private val item1 = Item(1, "Apples", 10.0, 20)
    private val item2 = Item(2, "Bananas", 15.0, 97)

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            .allowMainThreadQueries().build()
        itemDao = inventoryDatabase.itemDao()
    }

    @After
    fun closeDb() = inventoryDatabase.close()

    private suspend fun addOneItemToDb() = itemDao.insert(item1)
    private suspend fun addTwoItemsToDb() { itemDao.insert(item1); itemDao.insert(item2) }

    @Test
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        assertEquals(item1, itemDao.getAllItems().first()[0])
    }

    @Test
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        assertEquals(listOf(item1, item2), itemDao.getAllItems().first())
    }

    @Test
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        addTwoItemsToDb()
        val updated1 = Item(1, "Apples", 15.0, 25)
        val updated2 = Item(2, "Bananas", 5.0, 50)
        itemDao.update(updated1)
        itemDao.update(updated2)
        assertEquals(listOf(updated1, updated2), itemDao.getAllItems().first())
    }

    @Test
    fun daoDeleteItems_deletesAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        itemDao.delete(item1)
        itemDao.delete(item2)
        assertTrue(itemDao.getAllItems().first().isEmpty())
    }

    @Test
    fun daoGetItem_returnsItemFromDB() = runBlocking {
        addOneItemToDb()
        assertEquals(item1, itemDao.getItem(1).first())
    }
}
