package com.manish.mindora.di

import android.content.Context
import androidx.room.Room
import com.manish.mindora.BuildConfig
import com.manish.mindora.data.local.db.JournalEntryDao
import com.manish.mindora.data.local.db.MindoraDatabase
import com.manish.mindora.data.local.db.MindoraDatabaseMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "mindora.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MindoraDatabase {
        val builder = Room.databaseBuilder(
            context,
            MindoraDatabase::class.java,
            DB_NAME,
        ).addMigrations(*MindoraDatabaseMigrations.ALL)

        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration()
        }

        return builder.build()
    }

    @Provides
    fun provideJournalEntryDao(db: MindoraDatabase): JournalEntryDao = db.journalEntryDao()
}
