package com.example.domain_repository.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

/**
 * @Retention Annotations can have different retention policies that determine how they are stored and accessed.
 * The AnnotationRetention.BINARY retention policy specifically states that the annotation should be
 * retained during the compilation process and included in the compiled bytecode. This means that the
 * information provided by the annotation, such as the annotation's presence or any associated values,
 * will be available for tools or processes that analyze or manipulate the compiled code.
 * However, at runtime when the application is actually running, the annotation itself is not needed
 * It won't be accessible through reflection or other means of runtime introspection.
 *
 * @Qualifier  annotation is used to differentiate between multiple implementations of the same type
 * when performing dependency injection. It helps to disambiguate which specific implementation should
 * be used in a particular scenario.
 * In the code snippet you provided, there are two custom annotations that are marked with @Qualifier:
 * @QCharacterRepository and @QEpisodesRepository. These annotations are used as qualifiers to specify
 * which implementation should be injected for the corresponding repositories (CharacterRepository and
 * EpisodeRepository).
 * Here, the @QCharacterRepository annotation is used as a qualifier to specify that the
 * CharacterRepository interface should be bound to the CharacterRepositoryImpl implementation.
 * By using @Qualifier annotations, you can have multiple implementations of the same interface and
 * provide a way to differentiate between them. This becomes useful when you have different
 * requirements or conditions where different implementations need to be injected.
 */


    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class QCharacterRepository

