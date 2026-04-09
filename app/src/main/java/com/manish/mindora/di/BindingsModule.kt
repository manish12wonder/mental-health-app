package com.manish.mindora.di

import com.manish.mindora.data.feedback.AndroidFeedbackMessageProvider
import com.manish.mindora.data.repository.FirebaseAuthRepository
import com.manish.mindora.data.repository.JournalRepositoryImpl
import com.manish.mindora.data.sentiment.CompositeSentimentAnalyzer
import com.manish.mindora.domain.feedback.FeedbackMessageProvider
import com.manish.mindora.domain.repository.AuthRepository
import com.manish.mindora.domain.repository.JournalRepository
import com.manish.mindora.domain.sentiment.SentimentAnalyzer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds
    @Singleton
    abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFeedbackMessages(impl: AndroidFeedbackMessageProvider): FeedbackMessageProvider

    @Binds
    @Singleton
    abstract fun bindSentimentAnalyzer(impl: CompositeSentimentAnalyzer): SentimentAnalyzer
}
