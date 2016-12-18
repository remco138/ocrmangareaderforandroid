/*******************************************************************************
 * Copyright 2013-2016 Christopher Brochtrup
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.cb4960.dic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents 『大辞泉』 (Daijisen). J-J. Japanese-only example sentences.
 * 
 * Ported from JGlossator.
 */
public class DicEpwingDaijisen extends DicEpwing
{
  /** Constructor. */
  public DicEpwingDaijisen(String catalogsFile, int subBookIdx)
  {
    super(catalogsFile, subBookIdx);

    this.Name = "大辞泉";
    this.NameEng = "Daijisen";
    this.ShortName = "大辞泉";
    this.ExamplesNotes = "Japanese only";
    this.DicType = DicTypeEnum.JJ;
    this.ExamplesType = DicExamplesEnum.J_ONLY;
  }


  /** Lookup the given word. */
  @Override
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    // The list of entries in the dictionary for this word
    List<Entry> dicEntryList = new ArrayList<Entry>();
    List<String> rawEntryList = this.searchEpwingDic(word, EPLKUP_TAG_FLAG_LINK | EPLKUP_TAG_FLAG_SUB
        | EPLKUP_TAG_FLAG_SUP);

    for (String rawEntry : rawEntryList)
    {
      if (rawEntry.length() > 0)
      {
        Entry entry = new Entry();
        entry.SourceDic = this;

        List<String> lines = new LinkedList<String>(Arrays.asList(rawEntry.split("\n")));

        if (!this.parseFirstLine(lines.get(0), entry))
        {
          // Unexpected format
          continue;
        }

        // If user want to remove special characters from the reading, do it
        if (fineTune.JjRemoveSpecialReadingChars)
        {
          entry.Reading = entry.Reading.replaceAll("‐", "");
          entry.Reading = entry.Reading.replaceAll("・", "");
        }

        // Remove the line that was already parsed
        lines.remove(0);

        this.parseBody(lines, entry, includeExamples, fineTune);

        dicEntryList.add(entry);
      }
    }

    if (dicEntryList.size() == 0)
    {
      dicEntryList = null;
    }

    return dicEntryList;
  }


  /** Parse the body of the entry. */
  private void parseBody(List<String> lines, Entry entry, boolean includeExamples, FineTune fineTune)
  {
    int subDef = 1;
    boolean subDefFound = false;

    for (String line : lines)
    {
      String lineWithNewlines = line.replaceAll("([①-⑳㉑-㊿❶-❾㋐-㋾㊀-㊉◆Ⅰ-Ⅻ])", "\n$1");
      List<String> subLines = new LinkedList<String>(Arrays.asList(lineWithNewlines.split("\n")));

      for (String subLine : subLines)
      {
        String defLine = subLine;
        
        if(defLine.length() == 0)
        {
          continue;
        }

        // Skip image/video/audio symbols
        if (defLine.equals("[画像]") || defLine.equals("[動画]") || defLine.equals("♪"))
        {
          continue;
        }
      
        // Get the subdef number
        int newSubDef = UtilsLang.convertCircledNumToInteger(defLine.charAt(0));

        if (newSubDef != -1)
        {
          subDefFound = true;
          subDef = newSubDef;

          // Add a space between the subdef number and the def
          defLine = defLine.charAt(0) + " " + defLine.substring(1);
        }
        else if (subDefFound)
        {
          subDef = Example.NO_SUB_DEF;
        }

        // Remove worthless information (normally these would be appended to example sentences)
        defLine = defLine.replaceAll("〈.*?〉", "");

        // Remove the links from a line if the line doesn't contain other text and is not the only
        // line.
        defLine = this.removeLinks(defLine, (lines.size() == 1));

        // Parse out examples sentences
        List<String> exampleList = new ArrayList<String>();
        String defNoExamples = this.parseExamples(defLine, exampleList, entry.Expression,
            fineTune.JjFillInExampleBlanksWithWord);

        // Remove example sentences from definition unless user says otherwise
        if (!fineTune.JjKeepExamplesInDef)
        {
          defLine = defNoExamples;
        }

        for (String exampleText : exampleList)
        {
          Example example = new Example();
          example.Text = exampleText.trim();

          if (example.Text.length() > 0)
          {
            example.DicName = this.Name;
            example.SubDefNumber = subDef;
            example.HasTranslation = false;
            entry.ExampleList.add(example);
          }
        }

        // Add English space after fullstop to allow wrapping of long lines.
        defLine = defLine.replaceAll("。", "。 ");

        if (defLine.trim().length() > 0)
        {
          defLine += "<br />";
          entry.Definition += defLine.trim();
        }
      }
    }

    // Remove trailing <br />
    if (entry.Definition.endsWith("<br />"))
    {
      entry.Definition = entry.Definition.substring(0, entry.Definition.length() - 6);
    }
  }


  /** Remove the links from a line if the line doesn't contain other text and is not the only line. */
  private String removeLinks(String line, boolean onlyLine)
  {
    String defLine = line;
    String noLinkDef = defLine.replaceAll("<LINK>.*?</LINK.*?>", "").trim();

    // If the line would be blank after removing the links
    // and this was not the only line in the entry, remove the link text
    if ((noLinkDef.length() == 0) && !onlyLine)
    {
      // Remove the links
      defLine = noLinkDef;
    }
    else
    {
      // Remove the link tags, but keep the link text
      defLine = defLine.replaceAll("<LINK>", "");
      defLine = defLine.replaceAll("</LINK.*?>", "");
    }

    return defLine;
  }


  /**
   * Parse the examples from a line. Added to exampleList and returns the original line without the
   * examples.
   */
  public String parseExamples(String line, List<String> exampleList, String expression, boolean fillInBlanks)
  {
    // Match match;
    String noExamplesLine = line;
    int failsafe = 0;

    // Samples:
    // 1) 「not_ex」def。def。source1「ex1」「ex2」「ex3」
    // 2) 「not_ex」def。def。source1「ex1」「ex2」「ex3」→trailing。
    // .Net: @"(?<Example>(?<Source>[^。」]*?)「[^」]*?」。?)(?<Trailing>[↔→].*?)?$"
    Pattern pattern = Pattern.compile("(([^。」]*?)「[^」]*?」。?)([↔→].*?)?$");

    // Keep applying regex to get last example in line until no more examples are present.
    while (true)
    {
      Matcher matcher = pattern.matcher(noExamplesLine);

      if (matcher.find())
      {
        failsafe++;

        String example = matcher.group(1);
        String source = matcher.group(2);
        String trailing = matcher.group(3);

        if (example == null)
        {
          example = "";
        }

        if (source == null)
        {
          source = "";
        }

        if (trailing == null)
        {
          trailing = "";
        }

        example = example.trim();
        source = source.trim();
        trailing = trailing.trim();

        // Just in case
        if ((example.length() == 0) || (failsafe > 200))
        {
          break;
        }

        // Remove the example sentence, but leave in the trailing text
        noExamplesLine = noExamplesLine.substring(0, noExamplesLine.length() - example.length() - trailing.length())
            + noExamplesLine.substring(noExamplesLine.length() - trailing.length());
        noExamplesLine = noExamplesLine.trim();

        // Remove the source text
        if (source.length() > 0)
        {
          example = example.substring(source.length());
        }

        // Remove '「' and '」'
        example = example.replaceAll("「", "").replaceAll("」", "");

        // Handle blank part of example
        example = this.processExampleBlanks(example, expression, fillInBlanks);

        exampleList.add(example);
      }
      else
      {
        break;
      }
    }

    return noExamplesLine;
  }


  /**
   * If user has "Fill in example sentence blanks with expression" checked, 無罪を―する ---> ▲無罪を確信する
   * Otherwise, 無罪を―する ---> ▲無罪を___する
   */
  private String processExampleBlanks(String example, String expression, boolean fillInBlanks)
  {
    String newExample = example;

    // If user wants to fill in the blanks with the expression
    if (fillInBlanks)
    {
      String unformattedExpression = expression.replaceAll("・", "|");
      unformattedExpression = UtilsFormatting.removeSpecialCharsFromExpression(unformattedExpression);

      // Only use the first expression (零す・溢す ---> 零す)
      if (unformattedExpression.contains("|"))
      {
        try
        {
          unformattedExpression = unformattedExpression.substring(0, unformattedExpression.indexOf("|"));
        }
        catch (Exception e1)
        {
          // Don't care
        }
      }

      newExample = newExample.replaceAll("―", unformattedExpression);
    }
    else
    // Make expression placeholder more obvious
    {
      newExample = newExample.replaceAll("―", "___");
    }

    return newExample;
  }


  /**
   * Parse the first line of an entry. Sets the reading and expression of the entry. Will ignore
   * kanji entries (by design). Returns false on error.
   */
  private boolean parseFirstLine(String line, Entry entry)
  {
    boolean success = true;

    if (line.trim().length() == 0)
    {
      return false;
    }

    // .NET: (?:^(?<Reading>.*?)【(?<Expression>.*?)】)|(?:^(?<Reading>.*))
    Pattern pattern = Pattern.compile("(?:^(.*?)【(.*?)】)|(?:^(.*))");
    Matcher matcher = pattern.matcher(line);

    if (matcher.find())
    {
      entry.Reading = matcher.group(1);
      entry.Expression = matcher.group(2);

      if (entry.Reading == null)
      {
        entry.Reading = matcher.group(3);
      }

      if (entry.Reading == null)
      {
        entry.Reading = "";
      }

      if (entry.Expression == null)
      {
        entry.Expression = "";
      }

      entry.Reading = entry.Reading.trim();
      entry.Expression = entry.Expression.trim();

      // If the reading contains alpha characters, fail
      if ((entry.Reading.length() > 0) && UtilsLang.containsAlpha(entry.Reading.charAt(0)))
      {
        success = false;
      }
      else
      {
        String noHtml = entry.Expression.replaceAll("(?:<(.*?)>.*?</\\1>)|(?:<.*?/>)", "");

        // If the expression was not found or contains alpha characters
        if ((entry.Expression.length() == 0) || UtilsLang.containsAlpha(noHtml))
        {
          entry.Expression = entry.Reading.replaceAll("‐", "");

          // If the reading contains a kanji at this point, it is a kanji entry. Don't use these.
          if (UtilsLang.containsIdeograph(entry.Reading) || (entry.Expression.length() == 0))
          {
            success = false;
          }
        }
      }
    }
    else
    {
      success = false;
    }

    return success;
  }

}
