package com.halo.dictionary.mvp.impl;

import com.halo.dictionary.mvp.AddEntryPresenter;
import com.halo.dictionary.mvp.AddEntryView;
import com.halo.dictionary.repository.DictionaryRepository;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AddEntryPresenterImplTest {

    @Test
    public void testValidateInput() {

        final AddEntryPresenter presenter = new AddEntryPresenterImpl(mock(AddEntryView.class));

        assertTrue(presenter.validateInput("correct", "корректный"));
        verify(presenter.getView(), times(0)).showMessage(Mockito.anyString());

        assertTrue(presenter.validateInput("it's ok", ""));
        verify(presenter.getView(), times(0)).showMessage(Mockito.anyString());

        assertFalse(presenter.validateInput(null, "где слово"));
        verify(presenter.getView(), times(1)).showMessage(Mockito.anyString());

        assertFalse(presenter.validateInput("where is translation", null));
        verify(presenter.getView(), times(2)).showMessage(Mockito.anyString());

    }

    @Test(expected = IllegalStateException.class)
    public void testDetachView() {
        final AddEntryPresenter presenter = new AddEntryPresenterImpl(mock(AddEntryView.class));
        presenter.detachView();
        presenter.getView();
    }

    @Test
    public void testAttachView() {
        final AddEntryPresenter presenter = new AddEntryPresenterImpl(mock(AddEntryView.class));
        final AddEntryView view = Mockito.mock(AddEntryView.class);
        presenter.attachView(view);
        assertEquals(view, presenter.getView());

        final AddEntryPresenter otherPresenter = new AddEntryPresenterImpl(view);
        assertEquals(view, otherPresenter.getView());
    }

    @Test
    public void testOnSaveEntryCorrectInput() {

        final String word = "hello";
        final String translation = "привет";

        final AddEntryView view = Mockito.mock(AddEntryView.class);
        Mockito.when(view.getEnteredWord()).thenReturn(word);
        Mockito.when(view.getEnteredTranslation()).thenReturn(translation);

        final DictionaryRepository mockedRepository = mock(DictionaryRepository.class);
        final AddEntryPresenter presenter = new AddEntryPresenterImpl(view, mockedRepository);

        presenter.onSaveButtonClicked();

        verify(mockedRepository, times(1)).createEntry(word, translation, true);
        verify(view, times(1)).close();
    }


    @Test
    public void testOnSaveEntryIncorrectInput() {

        final AddEntryView view = Mockito.mock(AddEntryView.class);
        Mockito.when(view.getEnteredWord()).thenReturn(null);
        Mockito.when(view.getEnteredTranslation()).thenReturn("привет");

        final DictionaryRepository mockedRepository = mock(DictionaryRepository.class);
        final AddEntryPresenter presenter = new AddEntryPresenterImpl(view, mockedRepository);

        presenter.onSaveButtonClicked();

        verify(mockedRepository, times(0)).createEntry(anyString(), anyString(), anyBoolean());
        verify(view, times(0)).close();
    }
}