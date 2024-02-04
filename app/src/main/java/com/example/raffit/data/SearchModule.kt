package com.example.raffit.data

import android.content.Context
import com.example.raffit.data.bookmark.BookmarkRepository
import com.example.raffit.data.bookmark.BookmarkRepositoryImpl
import com.example.raffit.data.search.ApiRepository
import com.example.raffit.data.search.ApiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object SearchModule {

    @Provides
    fun provideApiRepository(): ApiRepository = ApiRepositoryImpl()

    @Provides
    fun provideBookmarkRepository(@ApplicationContext context: Context): BookmarkRepository = BookmarkRepositoryImpl(context)
}