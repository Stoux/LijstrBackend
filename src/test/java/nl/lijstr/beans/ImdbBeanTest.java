package nl.lijstr.beans;

import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.repositories.imdb.GenreRepository;
import nl.lijstr.repositories.imdb.PersonRepository;
import nl.lijstr.repositories.imdb.SpokenLanguageRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static nl.lijstr._TestUtils.TestUtils.getInvocationParam;
import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 23/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImdbBeanTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private SpokenLanguageRepository languageRepository;
    @Mock
    private PersonRepository personRepository;

    private ImdbBean imdbBean;

    @Before
    public void setUp() throws Exception {
        imdbBean = new ImdbBean();
        insertMocks(imdbBean, genreRepository, languageRepository, personRepository);

        when(genreRepository.saveAndFlush(any(Genre.class)))
                .thenAnswer(invocation -> getInvocationParam(invocation, 0));
        when(languageRepository.saveAndFlush(any(SpokenLanguage.class)))
                .thenAnswer(invocation -> getInvocationParam(invocation, 0));

        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> getInvocationParam(invocation, 0));
    }

    @Test
    public void nonExistentGenre() throws Exception {
        //Arrange
        when(genreRepository.getByGenre(anyString())).thenReturn(null);

        //Act
        Genre random = imdbBean.getOrCreateGenre("Random");

        //Assert
        assertEquals("Random", random.getGenre());
        verify(genreRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void existingLanguage() throws Exception {
        //Arrange
        String randomLanguage = "Memetic";
        SpokenLanguage language = new SpokenLanguage(randomLanguage);
        when(languageRepository.getByLanguage(eq(language.getLanguage())))
                .thenReturn(language);

        //Act
        SpokenLanguage foundLanguage = imdbBean.getOrCreateLanguage(randomLanguage);

        //Assert
        assertEquals(randomLanguage, foundLanguage.getLanguage());
        assertEquals(language, foundLanguage);
        verify(languageRepository, times(0)).saveAndFlush(any());
    }

    @Test
    public void getPerson() throws Exception {
        //Arrange
        Person person = new Person();
        when(personRepository.getByImdbId(anyString()))
                .thenReturn(person);

        //Act
        Person foundPerson = imdbBean.getPerson("imdbId");

        //Assert
        assertEquals(person, foundPerson);
    }

    @Test
    public void addPerson() throws Exception {
        //Arrange
        Person person = new Person();

        //Act
        Person savedPerson = imdbBean.addPerson(person);

        //Assert
        assertEquals(person, savedPerson);
        verify(personRepository, times(1)).save(eq(person));
    }

}