package edu.stanford.nlp.mt.decoder.efeat;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.nlp.util.Index;

import edu.stanford.nlp.mt.base.CacheableFeatureValue;
import edu.stanford.nlp.mt.base.ConcreteTranslationOption;
import edu.stanford.nlp.mt.base.FeatureValue;
import edu.stanford.nlp.mt.base.Featurizable;
import edu.stanford.nlp.mt.base.IString;
import edu.stanford.nlp.mt.base.Sequence;
import edu.stanford.nlp.mt.decoder.feat.IncrementalFeaturizer;
import edu.stanford.nlp.mt.decoder.feat.IsolatedPhraseFeaturizer;

/**
 * Adds features to the MT system based on the types of punctuation
 * produced in the target side.
 *
 *@author John Bauer
 */
public class TargetSidePunctuationFeaturizer implements IncrementalFeaturizer<IString,String>,
IsolatedPhraseFeaturizer<IString, String> {

  /**
   * All features will start with this prefix
   */
  public static final String FEATURE_NAME = "TgtPunc";

  public static final Pattern PUNCT_PATTERN = Pattern.compile("\\p{Punct}+");

  private final boolean addLexicalFeatures;
  private final boolean addDifferenceCounts;

  public TargetSidePunctuationFeaturizer() {
    addLexicalFeatures = true;
    addDifferenceCounts = false;
  }

  public TargetSidePunctuationFeaturizer(String...args) {
    addLexicalFeatures = args.length > 0 ? Boolean.parseBoolean(args[0]) : false;
    addDifferenceCounts = args.length > 1 ? Boolean.parseBoolean(args[1]) : false;
  }

  @Override
  public void initialize(Index<String> featureIndex) {
  }

  @Override
  public List<FeatureValue<String>> phraseListFeaturize(
      Featurizable<IString, String> f) {
    List<FeatureValue<String>> features = new LinkedList<FeatureValue<String>>();

    int nTargetSidePunctuationChars = 0;
    for (IString targetWord : f.targetPhrase) {
      String word = targetWord.toString();
      if (PUNCT_PATTERN.matcher(word).matches()) {
        if (addLexicalFeatures) {
          features.add(new CacheableFeatureValue<String>(FEATURE_NAME + "." + word.charAt(0), 1.0));
          features.add(new CacheableFeatureValue<String>(FEATURE_NAME, 1.0));
        }
        ++nTargetSidePunctuationChars;
      }
    }

    if (addDifferenceCounts) {
      int nSourceSidePunctuationChars = 0;
      for (IString sourceWord : f.sourcePhrase) {
        String word = sourceWord.toString();
        if (PUNCT_PATTERN.matcher(word).matches()) {
          ++nSourceSidePunctuationChars;
        }
      }

      int diff = nTargetSidePunctuationChars - nSourceSidePunctuationChars;
      if (diff > 0) {
        features.add(new CacheableFeatureValue<String>(FEATURE_NAME + ".ins", diff));
      } else if (diff < 0) {
        features.add(new CacheableFeatureValue<String>(FEATURE_NAME + ".del", -1 * diff));
      }
    }

    return features;
  }

  @Override
  public FeatureValue<String> phraseFeaturize(Featurizable<IString, String> f) {
    return null;
  }

  @Override
  public void initialize(
      List<ConcreteTranslationOption<IString, String>> options,
      Sequence<IString> foreign, Index<String> featureIndex) {
  }

  @Override
  public void reset() {
  }

  @Override
  public List<FeatureValue<String>> listFeaturize(
      Featurizable<IString, String> f) {
    return null;
  }

  @Override
  public FeatureValue<String> featurize(Featurizable<IString, String> f) {
    return null;
  }
}
